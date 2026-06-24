(function () {
    const aviso = document.getElementById("avisoProximamente");

    function mostrarProximamente(evento) {
        evento.preventDefault();
        if (!aviso) {
            return;
        }
        aviso.classList.add("visible");
        window.setTimeout(function () {
            aviso.classList.remove("visible");
        }, 2200);
    }

    document.querySelectorAll("[data-proximamente]").forEach(function (elemento) {
        elemento.addEventListener("click", mostrarProximamente);
    });

    document.querySelectorAll("[data-favorito]").forEach(function (boton) {
        boton.addEventListener("click", function () {
            const activo = boton.classList.toggle("activo");
            boton.textContent = activo ? "♥" : "♡";
        });
    });

    document.querySelectorAll("[data-toggle-password]").forEach(function (boton) {
        boton.addEventListener("click", function () {
            const campo = document.getElementById(boton.dataset.togglePassword);
            if (!campo) {
                return;
            }
            const mostrar = campo.type === "password";
            campo.type = mostrar ? "text" : "password";
            boton.textContent = mostrar ? "⊘" : "◎";
            boton.setAttribute("aria-label", mostrar ? "Ocultar contrasena" : "Mostrar contrasena");
        });
    });

    document.querySelectorAll("[data-menu-toggle]").forEach(function (boton) {
        boton.addEventListener("click", function () {
            const menu = document.getElementById("enlacesNavegacion");
            if (!menu) {
                return;
            }
            const abierto = menu.classList.toggle("abierto");
            boton.setAttribute("aria-expanded", String(abierto));
            boton.textContent = abierto ? "×" : "☰";
        });
    });
}());
