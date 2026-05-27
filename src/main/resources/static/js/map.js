// Variables globales
let map;
let userMarker;
let userLocation = null;
let restaurantMarkers = [];
let visitedRestaurants = [];
let allRestaurants = [];
let infoWindows = [];

// Intentaremos tomar los datos inyectados por Thymeleaf:
// map.html inyecta 'restaurants' con th:inline="javascript" (restaurantsJson).
// También mantenemos compatibilidad con una variable global 'restaurantes'.
let restaurantsRaw = window.restaurants || window.restaurantes || [];

/**
 * Normaliza la estructura de los restaurantes:
 * - asegura que cada objeto tenga `lat` y `lng`
 * - deja los demás campos tal cual
 */
function normalizeRestaurants(raw) {
    const defaultLat = 4.7110;
    const defaultLng = -74.0721;
    if (!Array.isArray(raw)) return [];
    return raw.map(r => {
        // r puede venir con .latitude/.longitude o .lat/.lng
        const lat = (r.lat !== undefined && r.lat !== null) ? Number(r.lat)
            : (r.latitude !== undefined && r.latitude !== null) ? Number(r.latitude)
                : defaultLat;
        const lng = (r.lng !== undefined && r.lng !== null) ? Number(r.lng)
            : (r.longitude !== undefined && r.longitude !== null) ? Number(r.longitude)
                : defaultLng;
        return Object.assign({}, r, { lat, lng });
    });
}

// Inicializamos la variable restaurants con datos normalizados
let restaurants = normalizeRestaurants(restaurantsRaw);

// Inicializar mapa
function initMap() {
    // Si google.maps no está definido, no intentamos proceder
    if (!window.google || !google.maps) {
        console.warn('Google Maps API no está disponible aún. Reintentando...');
        return;
    }

    // Ubicación por defecto (Bogotá, Colombia)
    const defaultLocation = { lat: 4.7110, lng: -74.0721 };

    // Crear mapa
    map = new google.maps.Map(document.getElementById('googleMap'), {
        zoom: 15,
        center: defaultLocation,
        mapTypeControl: false,
        fullscreenControl: false,
        zoomControl: true,
        zoomControlOptions: {
            position: google.maps.ControlPosition.RIGHT_CENTER
        }
    });

    // Obtener ubicación del usuario
    getGeolocation();

    // Agregar marcadores de restaurantes
    if (restaurants && restaurants.length > 0) {
        restaurants.forEach(restaurant => {
            addRestaurantMarker(restaurant, false);
        });
    }

    // Event listeners
    document.getElementById('centerMapBtn')?.addEventListener('click', centerMapOnUser);
    document.getElementById('showAllBtn')?.addEventListener('click', showAllRestaurants);
    document.getElementById('showVisitedBtn')?.addEventListener('click', showVisitedRestaurants);
    document.getElementById('mapSearch')?.addEventListener('input', filterRestaurants);
}

// Cuando el navegador provee geolocation
function getGeolocation() {
    if (navigator.geolocation) {
        navigator.geolocation.watchPosition(
            position => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                userLocation = { lat, lng };

                // Actualizar información de geolocalización
                updateGeoInfo(lat, lng);

                // Crear o actualizar marcador del usuario
                if (!userMarker && map) {
                    userMarker = new google.maps.Marker({
                        position: userLocation,
                        map: map,
                        title: 'Tu ubicación',
                        icon: createCustomIcon(),
                        zIndex: 1000
                    });

                    // Centrar mapa en ubicación del usuario la primera vez
                    map.setCenter(userLocation);
                    map.setZoom(15);
                } else if (userMarker) {
                    userMarker.setPosition(userLocation);
                }

                // Calcular distancias
                calculateDistances();
            },
            error => {
                console.error('Error obteniendo ubicación:', error);
                updateGeoInfo(null, null, 'Error: No se pudo obtener ubicación');
            },
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
    } else {
        updateGeoInfo(null, null, 'Geolocalización no disponible en tu navegador');
    }
}

function updateGeoInfo(lat, lng, message = null) {
    const geoInfo = document.getElementById('geoInfo');
    if (!geoInfo) return;
    if (message) {
        geoInfo.textContent = message;
    } else if (lat !== undefined && lng !== undefined && lat !== null && lng !== null) {
        geoInfo.textContent = `Tu ubicación: ${lat.toFixed(4)}, ${lng.toFixed(4)}`;
    } else {
        geoInfo.textContent = 'Ubicación no disponible';
    }
}

function createCustomIcon() {
    return {
        path: google.maps.SymbolPath.CIRCLE,
        scale: 8,
        fillColor: '#1d5cff',
        fillOpacity: 0.9,
        strokeColor: '#fff',
        strokeWeight: 2
    };
}

function addRestaurantMarker(restaurant, isVisited = false) {
    if (!restaurant || !map) return;

    const lat = restaurant.lat ?? 4.7110;
    const lng = restaurant.lng ?? -74.0721;

    const marker = new google.maps.Marker({
        position: { lat, lng },
        map: map,
        title: restaurant.name || 'Restaurante',
        icon: getRestaurantIcon(isVisited),
        zIndex: 100
    });

    const infoWindow = new google.maps.InfoWindow({
        content: createInfoWindowContent(restaurant),
        maxWidth: 320
    });

    marker.addListener('click', () => {
        closeAllInfoWindows();
        infoWindow.open(map, marker);
    });

    restaurantMarkers.push({ marker, restaurant, infoWindow });
    allRestaurants.push(restaurant);
    if (isVisited) visitedRestaurants.push(restaurant);

    return marker;
}

function getRestaurantIcon(isVisited) {
    if (isVisited) {
        return {
            path: google.maps.SymbolPath.CIRCLE,
            scale: 10,
            fillColor: '#12b76a',
            fillOpacity: 0.9,
            strokeColor: '#fff',
            strokeWeight: 2
        };
    } else {
        return {
            path: google.maps.SymbolPath.CIRCLE,
            scale: 10,
            fillColor: '#ff6b6b',
            fillOpacity: 0.9,
            strokeColor: '#fff',
            strokeWeight: 2
        };
    }
}

function createInfoWindowContent(restaurant) {
    const distance = restaurant.distanceKm ? `${Number(restaurant.distanceKm).toFixed(2)} km` : (restaurant.calculatedDistance ? `${restaurant.calculatedDistance} km` : 'N/A');
    const rating = restaurant.rating ? `⭐ ${restaurant.rating}` : 'Sin calificación';
    const name = restaurant.name || 'Restaurante';

    return `
        <div style="padding: 8px; max-width: 300px;">
            <div style="margin-bottom: 6px;">
                <h3 style="margin: 0 0 4px; font-size: 16px; font-weight: 600;">${escapeHtml(name)}</h3>
                <p style="margin: 0 0 2px; font-size: 13px; color: #667085;">${escapeHtml(restaurant.location || '')}</p>
            </div>
            <div style="display: flex; gap: 12px; margin-bottom: 6px; font-size: 13px; color: #475467;">
                <span>${rating}</span>
                <span>📍 ${distance}</span>
            </div>
            <p style="margin: 0 0 8px; font-size: 12px; color: #667085;">${escapeHtml(restaurant.cuisine || '')}</p>
            <a href="/restaurants/${restaurant.id}" style="
                display: inline-block;
                background: #1d5cff;
                color: white;
                padding: 6px 12px;
                border-radius: 8px;
                text-decoration: none;
                font-size: 12px;
                font-weight: 600;
                cursor: pointer;
            ">Ver detalles →</a>
        </div>
    `;
}

function escapeHtml(str) {
    return String(str || '').replace(/[&<>"'`=\/]/g, function (s) {
        return ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;', '/': '&#x2F;', '`': '&#x60;', '=': '&#x3D;'
        })[s];
    });
}

function closeAllInfoWindows() {
    infoWindows.forEach(iw => iw.close());
    restaurantMarkers.forEach(rm => rm.infoWindow.close());
}

function calculateDistances() {
    if (!userLocation || !window.google || !google.maps || !google.maps.geometry) return;

    restaurantMarkers.forEach(({ restaurant }) => {
        if (restaurant.lat && restaurant.lng) {
            const distance = google.maps.geometry.spherical.computeDistanceBetween(
                new google.maps.LatLng(userLocation.lat, userLocation.lng),
                new google.maps.LatLng(restaurant.lat, restaurant.lng)
            );
            restaurant.calculatedDistance = (distance / 1000).toFixed(2); // km
        }
    });

    updateRestaurantsList();
}

function centerMapOnUser() {
    if (userLocation && map) {
        map.setCenter(userLocation);
        map.setZoom(15);
    } else {
        alert('Ubicación no disponible aún');
    }
}

function showAllRestaurants() {
    document.getElementById('showAllBtn')?.classList.add('active');
    document.getElementById('showVisitedBtn')?.classList.remove('active');
    document.getElementById('visitedSection') && (document.getElementById('visitedSection').style.display = 'none');

    restaurantMarkers.forEach(({ marker }) => marker.setVisible(true));

    updateBadgeCount(restaurantMarkers.length);
    updateRestaurantsList();
}

function showVisitedRestaurants() {
    document.getElementById('showAllBtn')?.classList.remove('active');
    document.getElementById('showVisitedBtn')?.classList.add('active');
    document.getElementById('visitedSection') && (document.getElementById('visitedSection').style.display = 'block');

    restaurantMarkers.forEach(({ marker, restaurant }) => {
        const isVisited = visitedRestaurants.some(v => v.id === restaurant.id);
        marker.setVisible(isVisited);
    });

    updateBadgeCount(visitedRestaurants.length);
    loadVisitedRestaurantsList();
}

function filterRestaurants() {
    const searchTerm = (document.getElementById('mapSearch')?.value || '').toLowerCase();

    restaurantMarkers.forEach(({ marker, restaurant }) => {
        const name = (restaurant.name || '').toLowerCase();
        const location = (restaurant.location || '').toLowerCase();
        const cuisine = (restaurant.cuisine || '').toLowerCase();

        const matches = name.includes(searchTerm) || location.includes(searchTerm) || cuisine.includes(searchTerm);
        marker.setVisible(matches);
    });

    updateRestaurantsList();
}

function updateRestaurantsList() {
    const list = document.getElementById('restaurantsList');
    if (!list) return;
    const visibleMarkers = restaurantMarkers.filter(({ marker }) => marker.getVisible());

    list.innerHTML = visibleMarkers.map(({ restaurant }) => `
        <article class="restaurant-card mini" onclick="openRestaurant(${restaurant.id})">
            <img src="${escapeHtml(restaurant.heroImageUrl || '')}" alt="restaurant">
            <div class="restaurant-card-body">
                <div>
                    <h3>${escapeHtml(restaurant.name || '')}</h3>
                    <p>${escapeHtml(restaurant.location || '')}</p>
                </div>
                <div class="restaurant-score">
                    <span>⭐</span>
                    <strong>${restaurant.rating || 'N/A'}</strong>
                </div>
            </div>
            <div class="restaurant-footer">
                <span>${restaurant.calculatedDistance ? restaurant.calculatedDistance + ' km' : (restaurant.distanceKm ? restaurant.distanceKm + ' km' : 'N/A')}</span>
                <a href="/restaurants/${restaurant.id}">Abrir →</a>
            </div>
        </article>
    `).join('');

    updateBadgeCount(visibleMarkers.length);
}

function loadVisitedRestaurantsList() {
    const list = document.getElementById('visitedRestaurantsList');
    if (!list) return;

    if (visitedRestaurants.length === 0) {
        list.innerHTML = `
            <div style="text-align: center; padding: 20px; color: #667085;">
                <p>📭 Aún no has visitado restaurantes</p>
            </div>
        `;
        return;
    }

    list.innerHTML = visitedRestaurants.map(restaurant => `
        <article class="visited-restaurant-card">
            <div class="visited-restaurant-badge">✓ Visitado</div>
            <img src="${escapeHtml(restaurant.heroImageUrl || '')}" alt="restaurant">
            <div class="restaurant-card-body">
                <div>
                    <h3>${escapeHtml(restaurant.name || '')}</h3>
                    <p>${escapeHtml(restaurant.location || '')}</p>
                </div>
                <div class="restaurant-score">
                    <span>⭐</span>
                    <strong>${restaurant.rating || 'N/A'}</strong>
                </div>
            </div>
            <div class="restaurant-footer">
                <a href="/restaurants/${restaurant.id}">Ver detalles →</a>
            </div>
        </article>
    `).join('');
}

function updateBadgeCount(count) {
    const badge = document.getElementById('badge-count');
    if (badge) {
        badge.textContent = `${count} ${count === 1 ? 'Restaurante' : 'Restaurantes'}`;
    }
    const restaurantCount = document.getElementById('restaurantCount');
    if (restaurantCount) {
        restaurantCount.innerHTML = `📍 Mostrando <strong>${count}</strong> restaurantes cerca`;
    }
}

function openRestaurant(id) {
    window.location.href = `/restaurants/${id}`;
}

/**
 * Arranque seguro:
 * - espera a DOMContentLoaded
 * - normaliza datos de restaurants (en caso de que la plantilla cambie)
 * - espera a que google.maps esté disponible y luego arranca initMap
 */
document.addEventListener('DOMContentLoaded', () => {
    // Re-lee y normaliza cualquier dato global en caso de que haya sido actualizado
    restaurantsRaw = window.restaurants || window.restaurantes || restaurantsRaw;
    restaurants = normalizeRestaurants(restaurantsRaw);

    // Reintento para esperar a que Google Maps cargue
    (function waitForGoogle() {
        if (window.google && google.maps) {
            try {
                initMap();
            } catch (e) {
                console.error('Error inicializando mapa:', e);
            }
        } else {
            // si no hay key o el script no cargó, volver a intentar en 300ms (hasta que el usuario refresque)
            setTimeout(waitForGoogle, 300);
        }
    })();
});
