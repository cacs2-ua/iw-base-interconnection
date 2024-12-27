DELETE FROM parametros;
DELETE FROM mensajes;
DELETE FROM valoraciones_tecnico;
DELETE FROM usuarios;
DELETE FROM incidencias;
DELETE FROM estados_incidencia;
DELETE FROM tipos_usuario;
DELETE FROM pagos;
DELETE FROM estados_pago;
DELETE FROM tarjetas_pago;
DELETE FROM personas_contacto;
DELETE FROM comercios;
DELETE FROM paises;



INSERT INTO public.paises (id, nombre) VALUES (1, 'España');
INSERT INTO public.paises (id, nombre) VALUES (2, 'Francia');
INSERT INTO public.paises (id, nombre) VALUES (3, 'Alemania');
INSERT INTO public.paises (id, nombre) VALUES (4, 'Italia');
INSERT INTO public.paises (id, nombre) VALUES (5, 'Portugal');
INSERT INTO public.paises (id, nombre) VALUES (6, 'Reino Unido');
INSERT INTO public.paises (id, nombre) VALUES (7, 'Países Bajos');
INSERT INTO public.paises (id, nombre) VALUES (8, 'Bélgica');
INSERT INTO public.paises (id, nombre) VALUES (9, 'Suiza');
INSERT INTO public.paises (id, nombre) VALUES (10, 'Suecia');


INSERT INTO public.comercios (id, activo, api_key, cif, direccion, iban, nombre, pais, provincia, url_back, pais_id) VALUES (1, true, 'mi-api-key-12345', 'CIF123456', 'Calle Falsa 123', 'ES9121000418450200051332', 'Comercio Ejemplo', 'España', 'Madrid', 'https://comercio-ejemplo.com/back', 1);
INSERT INTO public.comercios (id, activo, api_key, cif, direccion, iban, nombre, pais, provincia, url_back, pais_id) VALUES (2, true, 'mi-api-key-12346', 'CIF123457', 'Calle Falsa 124', 'ES9121000418450200051333', 'Comercio Ejemplo 2', 'España', 'Madrid', 'https://comercio-ejemplo.com/back', 1);


INSERT INTO public.tipos_usuario (id, nombre) VALUES (1, 'administrador');
INSERT INTO public.tipos_usuario (id, nombre) VALUES (2, 'tecnico');
INSERT INTO public.tipos_usuario (id, nombre) VALUES (3, 'comercio');



INSERT INTO public.usuarios (id, activo, contrasenya, email, nombre, comercio_id, tipo_id) VALUES (1, true, '$2a$10$uEzYq5xTUFwUgBezRaJNvOr7n88Xt7dV.Ne.qg2Pb1K8WmgBNSgP2', 'admin-default@gmail.com', 'admin-default', 1, 1);
INSERT INTO public.usuarios (id, activo, contrasenya, email, nombre, comercio_id, tipo_id) VALUES (2, true, '$2a$10$r/UwgDJHaNd1iJoKHwh9we3q3YxXQlcHDqSJVzIR00sRtwrlRytfy', 'tecnico-default@gmail.com', 'tecnico-default', 1, 2);
INSERT INTO public.usuarios (id, activo, contrasenya, email, nombre, comercio_id, tipo_id) VALUES (3, true, '$2a$10$SeXSpZ0tRIRkWUf7gBeN1u7ykt7x3n0ndNq5Mc4OLlwkQAuOb3SRa', 'comercio-default@gmail.com', 'comercio-default', 1, 3);


