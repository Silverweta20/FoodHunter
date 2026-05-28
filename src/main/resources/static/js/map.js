let map;
let userMarker;
let userLocation = null;
let restaurantMarkers = [];
let allRestaurants = [];
let visitedRestaurants = [];

let restaurantsRaw = window.restaurantsList || [];

function normalizeRestaurants(raw) {
    const defaultLat = 4.7110;
    const defaultLng = -74.0721;

    if (!Array.isArray(raw)) return [];

    return raw.map(r => {
        const lat = (r.lat !== undefined && r.lat !== null) ? Number(r.lat)
            : (r.latitude !== undefined && r.latitude !== null) ? Number(r.latitude)
                : defaultLat;

        const lng = (r.lng !== undefined && r.lng !== null) ? Number(r.lng)
            : (r.longitude !== undefined && r.longitude !== null) ? Number(r.longitude)
                : defaultLng;

        return Object.assign({}, r, { lat, lng });
    });
}

let restaurants = normalizeRestaurants(restaurantsRaw);

function initMap() {
    if (!window.google || !google.maps) {
        console.warn('Google Maps API no disponible aún');
        return;
    }

    const defaultLocation = { lat: 4.7110, lng: -74.0721 };

    map = new google.maps.Map(document.getElementById('googleMap'), {
        zoom: 13,
        center: defaultLocation,
        mapTypeControl: false,
        fullscreenControl: false,
        zoomControl: true,
        zoomControlOptions: {
            position: google.maps.ControlPosition.RIGHT_CENTER
        }
    });

    getGeolocation();

    restaurants.forEach(restaurant => {
        addRestaurantMarker(restaurant);
    });

    updateRestaurantsList();

    document.getElementById('centerMapBtn')?.addEventListener('click', centerMapOnUser);
    document.getElementById('showAllBtn')?.addEventListener('click', showAllRestaurants);
    document.getElementById('showVisitedBtn')?.addEventListener('click', showVisitedRestaurants);
    document.getElementById('mapSearch')?.addEventListener('input', filterRestaurants);
}

function getGeolocation() {
    if (!navigator.geolocation) {
        updateGeoInfo(null, null, 'Geolocalización no disponible');
        return;
    }

    navigator.geolocation.watchPosition(
        position => {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;
            userLocation = { lat, lng };

            updateGeoInfo(lat, lng);

            if (!userMarker && map) {
                userMarker = new google.maps.Marker({
                    position: userLocation,
                    map: map,
                    title: 'Tu ubicación',
                    icon: createCustomIcon(),
                    zIndex: 1000
                });

                map.setCenter(userLocation);
                map.setZoom(14);
            } else if (userMarker) {
                userMarker.setPosition(userLocation);
            }

            calculateDistances();
        },
        error => {
            console.error('Error:', error);
            updateGeoInfo(null, null, 'No se pudo obtener ubicación');
        },
        { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
}

function updateGeoInfo(lat, lng, message = null) {
    const geoInfo = document.getElementById('geoInfo');
    if (!geoInfo) return;

    if (message) {
        geoInfo.textContent = message;
    } else if (lat && lng) {
        geoInfo.textContent = `📍 ${lat.toFixed(4)}, ${lng.toFixed(4)}`;
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

function getRestaurantIcon(isVisited) {
    return {
        path: google.maps.SymbolPath.CIRCLE,
        scale: 10,
        fillColor: isVisited ? '#12b76a' : '#ff6b6b',
        fillOpacity: 0.9,
        strokeColor: '#fff',
        strokeWeight: 2
    };
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function createInfoWindowContent(restaurant) {
    const distance = restaurant.calculatedDistance
        ? `${restaurant.calculatedDistance} km`
        : (restaurant.distanceKm ? `${Number(restaurant.distanceKm).toFixed(2)} km` : 'N/A');

    const rating = restaurant.rating ? `⭐ ${restaurant.rating}` : 'Sin calificación';
    const name = restaurant.name || 'Restaurante';

    return `
        <div style="padding: 8px; max-width: 300px;">
            <h3 style="margin: 0 0 4px; font-size: 16px; font-weight: 600;">${escapeHtml(name)}</h3>
            <p style="margin: 0 0 2px; font-size: 13px; color: #667085;">${escapeHtml(restaurant.location || '')}</p>
            <div style="display: flex; gap: 12px; margin-bottom: 6px; font-size: 13px;">
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
            ">Ver detalles →</a>
        </div>
    `;
}

function addRestaurantMarker(restaurant, isVisited = false) {
    if (!restaurant || !map) return;

    const lat = Number(restaurant.lat || restaurant.latitude || 4.7110);
    const lng = Number(restaurant.lng || restaurant.longitude || -74.0721);

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
        infoWindow.open(map, marker);
    });

    restaurantMarkers.push({ marker, restaurant, infoWindow });
    allRestaurants.push(restaurant);

    if (isVisited) {
        visitedRestaurants.push(restaurant);
    }
}

function closeAllInfoWindows() {
    restaurantMarkers.forEach(({ infoWindow }) => {
        infoWindow.close();
    });
}

function calculateDistances() {
    if (!userLocation || !window.google || !google.maps.geometry) return;

    restaurantMarkers.forEach(({ restaurant }) => {
        if (restaurant.lat && restaurant.lng) {
            const distance = google.maps.geometry.spherical.computeDistanceBetween(
                new google.maps.LatLng(userLocation.lat, userLocation.lng),
                new google.maps.LatLng(Number(restaurant.lat), Number(restaurant.lng))
            );
            restaurant.calculatedDistance = (distance / 1000).toFixed(2);
        }
    });

    updateRestaurantsList();
}

function centerMapOnUser() {
    if (userLocation && map) {
        map.setCenter(userLocation);
        map.setZoom(14);
    } else {
        alert('Ubicación no disponible aún');
    }
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

function updateRestaurantsList() {
    const list = document.getElementById('restaurantsList');
    if (!list) return;

    const visibleMarkers = restaurantMarkers.filter(({ marker }) => marker.getVisible());

    if (visibleMarkers.length === 0) return;

    list.innerHTML = visibleMarkers.map(({ restaurant }) => `
        <article class="restaurant-card mini">
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
                <a href="/restaurants/${restaurant.id}" class="view-btn">Abrir →</a>
            </div>
        </article>
    `).join('');

    updateBadgeCount(visibleMarkers.length);
}

function loadVisitedRestaurantsList() {
    const list = document.getElementById('visitedRestaurantsList');
    if (!list) return;

    if (visitedRestaurants.length === 0) {
        list.innerHTML = '<div style="text-align: center; padding: 20px; color: #667085;">No has visitado restaurantes aún</div>';
        return;
    }

    list.innerHTML = visitedRestaurants.map(restaurant => `
        <article class="restaurant-card mini">
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
                <a href="/restaurants/${restaurant.id}" class="view-btn">Abrir →</a>
            </div>
        </article>
    `).join('');
}

function showAllRestaurants() {
    document.getElementById('showAllBtn')?.classList.add('active');
    document.getElementById('showVisitedBtn')?.classList.remove('active');
    document.getElementById('visitedSection')?.style.setProperty('display', 'none');

    restaurantMarkers.forEach(({ marker }) => marker.setVisible(true));

    updateRestaurantsList();
}

function showVisitedRestaurants() {
    document.getElementById('showAllBtn')?.classList.remove('active');
    document.getElementById('showVisitedBtn')?.classList.add('active');
    document.getElementById('visitedSection')?.style.setProperty('display', 'block');

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

document.addEventListener('DOMContentLoaded', () => {
    restaurantsRaw = window.restaurantsList || [];
    restaurants = normalizeRestaurants(restaurantsRaw);

    let attempts = 0;
    const maxAttempts = 50;

    (function waitForGoogle() {
        if (window.google && google.maps) {
            try {
                initMap();
            } catch (e) {
                console.error('Error:', e);
            }
            return;
        }

        attempts++;
        if (attempts >= maxAttempts) {
            console.error('Google Maps no cargó');
            return;
        }

        setTimeout(waitForGoogle, 300);
    })();
});