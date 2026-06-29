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
            boton.classList.toggle("contrasena-visible", mostrar);
            boton.setAttribute("aria-pressed", String(mostrar));
            boton.setAttribute("aria-label", mostrar ? "Ocultar contrasena" : "Mostrar contrasena");
        });
    });

    document.querySelectorAll("[data-contador-caracteres]").forEach(function (campo) {
        const contador = document.getElementById(campo.dataset.contadorCaracteres);
        const maximo = Number(campo.getAttribute("maxlength")) || 0;

        if (!contador || !maximo) {
            return;
        }

        function actualizarContador() {
            contador.textContent = campo.value.length + " / " + maximo + " caracteres";
        }

        campo.addEventListener("input", actualizarContador);
        actualizarContador();
    });

    document.querySelectorAll("[data-edad-decimal]").forEach(function (campo) {
        const contenedor = campo.closest(".campo-numerico");
        const botonIncrementar = contenedor ? contenedor.querySelector("[data-stepper-increment]") : null;
        const botonDisminuir = contenedor ? contenedor.querySelector("[data-stepper-decrement]") : null;
        const minimo = Number(campo.dataset.min) || 0;
        const maximo = Number(campo.dataset.max) || 30;

        function normalizarEntrada() {
            let valor = campo.value.replace(/\./g, ",").replace(/[^\d,]/g, "");
            const partes = valor.split(",");
            const entero = partes[0] || "";
            const decimal = partes.length > 1 ? "," + partes.slice(1).join("").slice(0, 1) : "";
            valor = entero + decimal;

            const numero = Number(valor.replace(",", "."));
            if (!Number.isNaN(numero) && numero > maximo) {
                valor = String(maximo);
            }

            campo.value = valor;
        }

        function formatearEdad(numero) {
            const limitado = Math.min(maximo, Math.max(minimo, numero));
            return Number.isInteger(limitado)
                    ? String(limitado)
                    : String(limitado).replace(".", ",");
        }

        function cambiarEdad(delta) {
            const valorActual = Number(campo.value.replace(",", "."));
            const base = Number.isNaN(valorActual) ? 0 : valorActual;
            campo.value = formatearEdad(base + delta);
            campo.dispatchEvent(new Event("input", { bubbles: true }));
        }

        campo.addEventListener("input", normalizarEntrada);

        if (botonIncrementar) {
            botonIncrementar.addEventListener("click", function () {
                cambiarEdad(1);
            });
        }

        if (botonDisminuir) {
            botonDisminuir.addEventListener("click", function () {
                cambiarEdad(-1);
            });
        }
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
