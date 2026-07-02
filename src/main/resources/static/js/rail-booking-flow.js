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

    const seatOptions = Array.from(document.querySelectorAll('.rj-seat-option'));
    const checkboxes = Array.from(document.querySelectorAll('.rail-seat-checkbox'));

    const passengerCountInput = document.getElementById('passengerCountInput');
    const selectedList = document.getElementById('selectedSeatList');
    const selectedCountText = document.getElementById('selectedCountText');
    const selectedTotalText = document.getElementById('selectedTotalText');

    const coachTabs = document.getElementById('railCoachTabs');
    const previousCoachBtn = document.getElementById('previousCoachBtn');
    const nextCoachBtn = document.getElementById('nextCoachBtn');

    const currentCoachName = document.getElementById('currentCoachName');
    const currentCoachMeta = document.getElementById('currentCoachMeta');
    const currentCoachSeatCount = document.getElementById('currentCoachSeatCount');

    const seatMap = document.querySelector('.rj-seat-map');

    const basePrice = Number(form.dataset.basePrice || 0);

    let coaches = [];
    let activeCoachIndex = 0;

    function formatVnd(value) {
        return new Intl.NumberFormat('en-US').format(value) + ' VND';
    }

    function numberValue(value) {
        const parsed = Number(value);
        return Number.isNaN(parsed) ? 0 : parsed;
    }

    function getSelectedCheckboxes() {
        return checkboxes.filter(function (checkbox) {
            return checkbox.checked;
        });
    }

    function formatCoachPrice(value) {
        if (!value || value <= 0) {
            return 'Included';
        }

        return '+' + formatVnd(value);
    }

    function buildCoaches() {
        const coachMap = new Map();

        seatOptions.forEach(function (option) {
            const checkbox = option.querySelector('.rail-seat-checkbox');

            if (!checkbox) {
                return;
            }

            const coachId = option.dataset.coachId || checkbox.dataset.coachId || '0';
            const coachNumber = option.dataset.coachNumber || checkbox.dataset.coachNumber || 'Unknown';
            const coachType = option.dataset.coachType || checkbox.dataset.coachType || 'Standard';
            const coachCapacity = option.dataset.coachCapacity || '0';
            const extraPrice = numberValue(checkbox.dataset.seatExtraPrice);

            if (!coachMap.has(coachId)) {
                coachMap.set(coachId, {
                    id: coachId,
                    number: coachNumber,
                    type: coachType,
                    capacity: coachCapacity,
                    totalSeats: 0,
                    availableSeats: 0,
                    minExtraPrice: extraPrice,
                    maxExtraPrice: extraPrice
                });
            }

            const coach = coachMap.get(coachId);

            coach.totalSeats += 1;

            if (!option.classList.contains('booked')) {
                coach.availableSeats += 1;
            }

            coach.minExtraPrice = Math.min(coach.minExtraPrice, extraPrice);
            coach.maxExtraPrice = Math.max(coach.maxExtraPrice, extraPrice);
        });

        coaches = Array.from(coachMap.values()).sort(function (a, b) {
            return String(a.number).localeCompare(String(b.number), undefined, {
                numeric: true,
                sensitivity: 'base'
            });
        });
    }

    function getCoachPriceText(coach) {
        if (!coach) {
            return 'Included';
        }

        if (coach.minExtraPrice === coach.maxExtraPrice) {
            return formatCoachPrice(coach.minExtraPrice);
        }

        return formatCoachPrice(coach.minExtraPrice) + ' - ' + formatCoachPrice(coach.maxExtraPrice);
    }

    function renderCoachTabs() {
        if (!coachTabs) {
            return;
        }

        coachTabs.innerHTML = '';

        coaches.forEach(function (coach, index) {
            const button = document.createElement('button');

            button.type = 'button';
            button.className = 'rj-coach-tab';
            button.dataset.coachId = coach.id;

            button.innerHTML =
                '<i class="bi bi-train-front"></i>' +
                '<div class="rj-coach-tab-content">' +
                '<strong>Coach ' + coach.number + '</strong>' +
                '<small>' + coach.type + ' • ' + coach.availableSeats + '/' + coach.totalSeats + ' available</small>' +
                '<em class="rj-coach-price">Coach price: ' + getCoachPriceText(coach) + '</em>' +
                '</div>';

            button.addEventListener('click', function () {
                showCoach(index);
            });

            coachTabs.appendChild(button);
        });
    }

    function showCoach(index) {
        if (!coaches.length) {
            return;
        }

        if (index < 0) {
            index = coaches.length - 1;
        }

        if (index >= coaches.length) {
            index = 0;
        }

        activeCoachIndex = index;

        const activeCoach = coaches[activeCoachIndex];

        seatOptions.forEach(function (option) {
            const sameCoach = option.dataset.coachId === activeCoach.id;
            option.classList.toggle('hidden-by-coach', !sameCoach);
        });

        document.querySelectorAll('.rj-coach-tab').forEach(function (button) {
            button.classList.toggle('active', button.dataset.coachId === activeCoach.id);
        });

        if (currentCoachName) {
            currentCoachName.textContent = 'Coach ' + activeCoach.number;
        }

        if (currentCoachMeta) {
            currentCoachMeta.textContent =
                activeCoach.type +
                ' • Coach price: ' +
                getCoachPriceText(activeCoach) +
                ' • ' +
                activeCoach.availableSeats +
                ' available of ' +
                activeCoach.totalSeats +
                ' seats';
        }

        if (currentCoachSeatCount) {
            currentCoachSeatCount.textContent = activeCoach.availableSeats + ' available';
        }

        if (previousCoachBtn) {
            previousCoachBtn.disabled = coaches.length <= 1;
        }

        if (nextCoachBtn) {
            nextCoachBtn.disabled = coaches.length <= 1;
        }

        if (seatMap) {
            seatMap.scrollTop = 0;
        }
    }

    function updateSeatClasses() {
        checkboxes.forEach(function (checkbox) {
            const option = checkbox.closest('.rj-seat-option');

            if (!option) {
                return;
            }

            option.classList.toggle('selected', checkbox.checked);
        });
    }

    function getSeatExtraPrice(checkbox) {
        return numberValue(checkbox.dataset.seatExtraPrice);
    }

    function getSeatTotalPrice(checkbox) {
        return basePrice + getSeatExtraPrice(checkbox);
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

        const total = selected.reduce(function (sum, checkbox) {
            return sum + getSeatTotalPrice(checkbox);
        }, 0);

        if (selectedTotalText) {
            selectedTotalText.textContent = formatVnd(total);
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
            const coachNumber = checkbox.dataset.coachNumber || 'Unknown';
            const coachType = checkbox.dataset.coachType || seatType;
            const extraPrice = getSeatExtraPrice(checkbox);

            const item = document.createElement('div');
            item.className = 'rail-selected-item';

            item.innerHTML =
                '<div class="rail-selected-seat-info">' +
                '<div class="rail-selected-seat-code">' + seatLabel + '</div>' +
                '<div class="rail-selected-seat-type">' +
                '<strong>Coach ' + coachNumber + '</strong>' +
                '<small>' + coachType + '</small>' +
                '</div>' +
                '</div>' +
                '<div class="rail-selected-price">' +
                '<span class="rail-selected-extra-label">Coach price</span>' +
                '<strong>' + formatCoachPrice(extraPrice) + '</strong>' +
                '</div>';

            selectedList.appendChild(item);
        });
    }

    if (previousCoachBtn) {
        previousCoachBtn.addEventListener('click', function () {
            showCoach(activeCoachIndex - 1);
        });
    }

    if (nextCoachBtn) {
        nextCoachBtn.addEventListener('click', function () {
            showCoach(activeCoachIndex + 1);
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

        return true;
    });

    buildCoaches();
    renderCoachTabs();
    showCoach(0);
    updateSummary();
}