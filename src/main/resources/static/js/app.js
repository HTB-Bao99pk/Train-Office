// static/js/app.js

document.addEventListener("DOMContentLoaded", function () {
    initToast();
    initConfirmButtons();
    initTableSearch();
});

function initToast() {
    const toastEl = document.getElementById("appToast");
    if (toastEl && toastEl.dataset.show === "true") {
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }
}

function initConfirmButtons() {
    document.querySelectorAll("[data-confirm]").forEach(button => {
        button.addEventListener("click", function (event) {
            event.preventDefault();

            const targetUrl = this.getAttribute("href") || this.dataset.url;
            const message = this.dataset.confirm || "Are you sure?";

            const modalEl = document.getElementById("confirmModal");
            const confirmBtn = document.getElementById("confirmActionBtn");

            if (!modalEl || !confirmBtn) {
                if (confirm(message)) {
                    window.location.href = targetUrl;
                }
                return;
            }

            modalEl.querySelector(".modal-body").innerText = message;

            confirmBtn.onclick = function () {
                window.location.href = targetUrl;
            };

            const modal = new bootstrap.Modal(modalEl);
            modal.show();
        });
    });
}

function initTableSearch() {
    document.querySelectorAll("[data-table-search]").forEach(input => {
        input.addEventListener("keyup", function () {
            const tableSelector = this.dataset.tableSearch;
            const table = document.querySelector(tableSelector);
            const keyword = this.value.toLowerCase();

            if (!table) return;

            table.querySelectorAll("tbody tr").forEach(row => {
                const text = row.innerText.toLowerCase();
                row.style.display = text.includes(keyword) ? "" : "none";
            });
        });
    });
}

function formatCurrencyVND(value) {
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND"
    }).format(value);
}