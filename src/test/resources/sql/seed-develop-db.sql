DELETE FROM parametros_comercio;
DELETE FROM pedido_completados;



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

