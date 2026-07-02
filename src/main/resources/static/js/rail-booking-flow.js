document.addEventListener('DOMContentLoaded', function () {
    setupPassengerValidation();
    setupSeatSelection();
});

function setupPassengerValidation() {
    const passengerCards = document.querySelectorAll('.rail-passenger-card');

    if (!passengerCards.length) {
        return;
    }

    function getAge(dobString) {
        if (!dobString) {
            return -1;
        }

        const today = new Date();
        const birthDate = new Date(dobString);

        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDiff = today.getMonth() - birthDate.getMonth();

        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }

        return age;
    }

    function validateCard(card) {
        const dobInput = card.querySelector('.dob-input');
        const typeSelect = card.querySelector('.passenger-type-select');
        const errorMsg = card.querySelector('.age-validation-msg');

        const identityWrapper = card.querySelector('.identity-wrapper');
        const identityInput = card.querySelector('.identity-input');

        const relationshipWrapper = card.querySelector('.relationship-wrapper');
        const relationshipInput = card.querySelector('.relationship-input');

        if (!dobInput || !typeSelect) {
            return;
        }

        const age = getAge(dobInput.value);
        const type = typeSelect.value;

        let valid = true;

        if (age >= 0) {
            if (type === 'INFANT' && age >= 6) {
                valid = false;
            }

            if (type === 'CHILD' && (age < 6 || age > 10)) {
                valid = false;
            }

            if (type === 'ADULT' && (age < 11 || age >= 60)) {
                valid = false;
            }

            if (type === 'SENIOR' && age < 60) {
                valid = false;
            }
        }

        if (errorMsg) {
            errorMsg.classList.toggle('show', !valid);
        }

        typeSelect.classList.toggle('is-invalid', !valid);
        dobInput.classList.toggle('is-invalid', !valid);

        if (identityWrapper && identityInput) {
            if (type === 'INFANT' || type === 'CHILD') {
                identityWrapper.style.display = 'none';
                identityInput.required = false;
                identityInput.value = '';
            } else {
                identityWrapper.style.display = 'block';
                identityInput.required = true;
            }
        }

        if (relationshipWrapper && relationshipInput) {
            if (age >= 0 && age < 16) {
                relationshipWrapper.style.display = 'block';
                relationshipInput.required = true;
            } else {
                relationshipWrapper.style.display = 'none';
                relationshipInput.required = false;
                relationshipInput.value = '';
            }
        }
    }

    passengerCards.forEach(function (card) {
        const dobInput = card.querySelector('.dob-input');
        const typeSelect = card.querySelector('.passenger-type-select');

        if (dobInput) {
            dobInput.addEventListener('change', function () {
                validateCard(card);
            });
        }

        if (typeSelect) {
            typeSelect.addEventListener('change', function () {
                validateCard(card);
            });
        }

        validateCard(card);
    });
}

function setupSeatSelection() {
    const form = document.getElementById('railSeatSelectionForm');

    if (!form) {
        return;
    }

    const checkboxes = Array.from(document.querySelectorAll('.rail-seat-checkbox'));
    const passengerCountInput = document.getElementById('passengerCountInput');
    const selectedList = document.getElementById('selectedSeatList');
    const selectedCountText = document.getElementById('selectedCountText');
    const selectedTotalText = document.getElementById('selectedTotalText');

    const basePrice = Number(form.dataset.basePrice || 0);

    function formatVnd(value) {
        return new Intl.NumberFormat('en-US').format(value) + ' VND';
    }

    function getSelectedCheckboxes() {
        return checkboxes.filter(function (checkbox) {
            return checkbox.checked;
        });
    }

    function updateSeatClasses() {
        checkboxes.forEach(function (checkbox) {
            const option = checkbox.closest('.rail-seat-option');

            if (!option) {
                return;
            }

            option.classList.toggle('selected', checkbox.checked);
        });
    }

    function updateSummary() {
        const selected = getSelectedCheckboxes();

        updateSeatClasses();

        if (passengerCountInput) {
            passengerCountInput.value = selected.length;
        }

        if (selectedCountText) {
            selectedCountText.textContent = selected.length;
        }

        if (selectedTotalText) {
            selectedTotalText.textContent = formatVnd(selected.length * basePrice);
        }

        if (!selectedList) {
            return;
        }

        selectedList.innerHTML = '';

        if (!selected.length) {
            const empty = document.createElement('div');
            empty.className = 'rail-selected-empty';
            empty.textContent = 'No seats selected yet.';
            selectedList.appendChild(empty);
            return;
        }

        selected.forEach(function (checkbox) {
            const seatLabel = checkbox.dataset.seatLabel || 'Seat';
            const seatType = checkbox.dataset.seatType || 'Standard';

            const item = document.createElement('div');
            item.className = 'rail-selected-item';

            item.innerHTML =
                '<div class="rail-selected-seat-info">' +
                '<div class="rail-selected-seat-code">' + seatLabel + '</div>' +
                '<div class="rail-selected-seat-type">' +
                '<strong>Passenger</strong>' +
                '<small>' + seatType + '</small>' +
                '</div>' +
                '</div>' +
                '<div class="rail-selected-price">' + formatVnd(basePrice) + '</div>';

            selectedList.appendChild(item);
        });
    }

    checkboxes.forEach(function (checkbox) {
        checkbox.addEventListener('change', updateSummary);
    });

    form.addEventListener('submit', function (event) {
        const selected = getSelectedCheckboxes();

        if (!selected.length) {
            event.preventDefault();
            alert('Please select at least one seat.');
            return false;
        }

        if (passengerCountInput) {
            passengerCountInput.value = selected.length;
        }
    });

    updateSummary();
}