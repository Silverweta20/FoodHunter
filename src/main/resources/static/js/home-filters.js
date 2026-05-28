const queryInput = document.getElementById('queryInput');
const priceFilter = document.getElementById('priceFilter');
const cuisineFilter = document.getElementById('cuisineFilter');
const distanceFilter = document.getElementById('distanceFilter');
const availableOnly = document.getElementById('availableOnly');
const restaurantsGrid = document.getElementById('restaurantsGrid');
const resultCount = document.getElementById('resultCount');

function buildParams() {
    const params = new URLSearchParams();
    if (queryInput && queryInput.value.trim()) params.set('q', queryInput.value.trim());
    if (priceFilter && priceFilter.value) params.set('maxPrice', priceFilter.value);
    if (cuisineFilter && cuisineFilter.value) params.set('cuisine', cuisineFilter.value);
    if (distanceFilter && distanceFilter.value) params.set('maxDistanceKm', distanceFilter.value);
    if (availableOnly && availableOnly.checked) params.set('availableOnly', 'true');
    return params.toString();
}

function renderCards(items) {
    restaurantsGrid.innerHTML = items.map(r => `
        <a href="/restaurants/${r.id}" class="restaurant-card" onclick="navigateToRestaurant(${r.id}); return false;">
            <img src="${r.heroImageUrl}" alt="restaurant">
            <div class="restaurant-card-body">
                <div>
                    <h3>${r.name}</h3>
                    <p>${r.location}</p>
                    <small>${r.cuisine}</small>
                </div>
                <div class="restaurant-score">
                    <span>⭐</span>
                    <strong>${Number(r.rating).toFixed(1)}</strong>
                </div>
            </div>
            <div class="restaurant-footer">
                <span>Ranking: #${r.rankingPosition}</span>
                <span class="view-btn">Ver →</span>
            </div>
        </a>
    `).join('');

    if (resultCount) {
        resultCount.innerHTML = `Mostrando <strong>${items.length}</strong> restaurantes cerca`;
    }
}

// Función para navegar al detalle del restaurante
function navigateToRestaurant(restaurantId) {
    window.location.href = `/restaurants/${restaurantId}`;
}

async function refreshRestaurants() {
    try {
        const params = buildParams();
        const response = await fetch(`/api/restaurants/search?${params}`);
        if (!response.ok) throw new Error('Error en la búsqueda');
        const data = await response.json();
        renderCards(data);
    } catch (error) {
        console.error('Error al actualizar restaurantes:', error);
    }
}

let timer = null;
function scheduleRefresh() {
    clearTimeout(timer);
    timer = setTimeout(refreshRestaurants, 220);
}

// Event listeners
if (queryInput) queryInput.addEventListener('input', scheduleRefresh);
if (priceFilter) priceFilter.addEventListener('change', refreshRestaurants);
if (cuisineFilter) cuisineFilter.addEventListener('change', refreshRestaurants);
if (distanceFilter) distanceFilter.addEventListener('change', refreshRestaurants);
if (availableOnly) availableOnly.addEventListener('change', refreshRestaurants);

// Cargar restaurantes inicialmente
document.addEventListener('DOMContentLoaded', function() {
    refreshRestaurants();
});
