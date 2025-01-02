-- Ajuste unificado de secuencias:
SELECT setval('public.parametros_comercio_id_seq', COALESCE((SELECT MAX(id) FROM public.parametros_comercio), 0) + 1, false);
SELECT setval('public.pedido_completados_id_seq', COALESCE((SELECT MAX(id) FROM public.pedido_completados), 0) + 1, false);
