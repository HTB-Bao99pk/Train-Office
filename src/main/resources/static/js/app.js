document.addEventListener('DOMContentLoaded',function(){document.querySelectorAll('.toast.show').forEach(function(el){if(window.bootstrap&&bootstrap.Toast){new bootstrap.Toast(el,{delay:3500}).show();}});});

function togglePassword(inputId, icon) {
    const input = document.getElementById(inputId);

    if (!input) return;

    if (input.type === "password") {
        input.type = "text";
        icon.classList.remove("fa-eye");
        icon.classList.add("fa-eye-slash");
    } else {
        input.type = "password";
        icon.classList.remove("fa-eye-slash");
        icon.classList.add("fa-eye");
    }
}