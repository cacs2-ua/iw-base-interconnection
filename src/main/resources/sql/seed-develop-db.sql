DELETE FROM parametros_comercio;
DELETE FROM pedido_completados;
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



INSERT INTO public.parametros_comercio (id, clave, valor) VALUES (1, 'apiKey', 'mi-api-key-12346');
INSERT INTO public.parametros_comercio (id, clave, valor) VALUES (2, 'url_back', 'http://localhost:8246/tienda/receivePedido');

INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (1, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 29, 4, '1111111111111112', '1111111111111112', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (2, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 30, 4, '8484848484848484', '8484848484848484', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (3, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 31, 4, '11111111111112222', '11111111111112222', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (4, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 32, 4, '1111111111111111', '1111111111111111', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (5, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 33, 4, '1111111111111111', '1111111111111111', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (6, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 34, 4, '0000000000000000', '0000000000000000', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (7, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 35, 4, '0000000000000000', '0000000000000000', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (8, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 36, 4, '1010101010101010', '1010101010101010', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (9, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 37, 4, '1111111111111666', '1111111111111666', 'TICKET-888');
INSERT INTO public.pedido_completados (id, comercio_nombre, estado_pago, fecha, importe, pago_id, pedido_id, tarjeta, tarjeta_pago_numero, ticket_ext) VALUES (10, 'Comercio Ejemplo 2', 'acept001', '2029-09-09', 888.888, 38, 4, '1111111111111111', '1111111111111111', 'TICKET-888');

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


