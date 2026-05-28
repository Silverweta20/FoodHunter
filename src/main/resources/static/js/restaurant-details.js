// ====== TABS DE MENÚ POR TIPO ======
document.addEventListener('DOMContentLoaded', function() {
    const mealTabs = document.querySelectorAll('.meal-tab');
    const mealSections = document.querySelectorAll('.meal-section-content');

    // Inicializar: mostrar Desayuno por defecto
    document.querySelector('[data-meal="Desayuno"]')?.classList.add('active');

    mealTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const mealType = this.getAttribute('data-meal');

            // Actualizar tabs
            mealTabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            // Actualizar secciones
            mealSections.forEach(section => {
                section.classList.remove('active');
                if (section.getAttribute('data-meal') === mealType) {
                    section.classList.add('active');
                }
            });
        });
    });
});

// ====== ESTRELLAS INTERACTIVAS PARA RESEÑA ======
document.addEventListener('DOMContentLoaded', function() {
    const stars = document.querySelectorAll('.star-rating-input .star');
    const starsInput = document.getElementById('starsInput');
    const ratingText = document.getElementById('ratingText');

    const ratingLabels = {
        1: 'Malo',
        2: 'Regular',
        3: 'Bueno',
        4: 'Muy bueno',
        5: 'Excelente'
    };

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const value = this.getAttribute('data-value');
            starsInput.value = value;
            ratingText.textContent = ratingLabels[value];
            updateStars(value);
        });

        star.addEventListener('mouseenter', function() {
            const value = this.getAttribute('data-value');
            updateStars(value);
        });
    });

    document.getElementById('starRatingInput').addEventListener('mouseleave', function() {
        const currentValue = starsInput.value;
        updateStars(currentValue);
    });

    function updateStars(value) {
        stars.forEach(star => {
            const starValue = star.getAttribute('data-value');
            if (starValue <= value) {
                star.classList.add('active');
            } else {
                star.classList.remove('active');
            }
        });
    }

    // Inicializar con valor por defecto
    updateStars(starsInput.value);
});

// ====== MODAL DE FOTOS AMPLIADAS ======
document.addEventListener('DOMContentLoaded', function() {
    const photoModal = document.getElementById('photoModal');
    const photoModalImage = document.getElementById('photoModalImage');
    const photoModalClose = document.querySelector('.photo-modal-close');
    const photoThumbnails = document.querySelectorAll('.photo-thumbnail');

    photoThumbnails.forEach(thumbnail => {
        thumbnail.addEventListener('click', function() {
            const src = this.getAttribute('data-src');
            photoModalImage.src = src;
            photoModal.classList.add('active');
            document.body.style.overflow = 'hidden';
        });
    });

    photoModalClose.addEventListener('click', function() {
        photoModal.classList.remove('active');
        document.body.style.overflow = 'auto';
    });

    photoModal.addEventListener('click', function(e) {
        if (e.target === photoModal) {
            photoModal.classList.remove('active');
            document.body.style.overflow = 'auto';
        }
    });
});

// ====== MODAL DE DETALLES DEL PLATO ======
document.addEventListener('DOMContentLoaded', function() {
    const dishDetailModal = document.getElementById('dishDetailModal');
    const modalClose = document.querySelector('.modal-close');
    const viewDetailButtons = document.querySelectorAll('.btn-view-detail');

    // Datos de ejemplo para ingredientes (en producción, vendrían del servidor)
    const ingredientsData = {
        default: [
            { name: 'Queso extra', price: 2000 },
            { name: 'Jamón', price: 3000 },
            { name: 'Tomate', price: 1500 },
            { name: 'Lechuga', price: 1000 }
        ]
    };

    viewDetailButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const itemId = this.getAttribute('data-item-id');
            const itemName = this.getAttribute('data-item-name');
            const itemPrice = this.getAttribute('data-item-price');
            const itemImage = this.getAttribute('data-item-image');

            // Rellenar modal
            document.getElementById('dishDetailName').textContent = itemName;
            document.getElementById('dishDetailImage').src = itemImage;
            document.getElementById('dishDetailPrice').textContent = itemPrice;

            // Cargar ingredientes (simulado)
            const ingredientsContainer = document.getElementById('ingredientsContainer');
            ingredientsContainer.innerHTML = ingredientsData.default.map((ing, idx) => `
                <div class="ingredient-item">
                    <input type="checkbox" name="ingredients" value="${ing.name}" id="ing-${idx}">
                    <label for="ing-${idx}" style="flex: 1; cursor: pointer;">${ing.name}</label>
                    <span class="ingredient-price">+${ing.price}</span>
                </div>
            `).join('');

            // Mostrar modal
            dishDetailModal.classList.add('active');
            document.body.style.overflow = 'hidden';
        });
    });

    modalClose.addEventListener('click', function() {
        dishDetailModal.classList.remove('active');
        document.body.style.overflow = 'auto';
    });

    dishDetailModal.addEventListener('click', function(e) {
        if (e.target === dishDetailModal) {
            dishDetailModal.classList.remove('active');
            document.body.style.overflow = 'auto';
        }
    });

    // Manejar envío del formulario
    const dishOrderForm = document.getElementById('dishOrderForm');
    if (dishOrderForm) {
        dishOrderForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const selectedIngredients = Array.from(document.querySelectorAll('.ingredient-item input:checked'))
                .map(inp => inp.value);
            console.log('Plato con ingredientes:', selectedIngredients);
            // Aquí iría la lógica de agregar al carrito
            dishDetailModal.classList.remove('active');
            document.body.style.overflow = 'auto';
            alert('Plato agregado al carrito');
        });
    }
});
