
INSERT INTO clientes (id, rut, nombre, correo, telefono, estado, activo) VALUES
(1, '76.543.210-K', 'Comercial Los Aromos Ltda.', 'compras@losaromos.cl', '+56 9 1234 5678', 'ACTIVO', true),
(2, '77.123.456-7', 'Distribuidora del Sur SpA', 'contacto@distsur.cl', '+56 9 8765 4321', 'ACTIVO', true),
(3, '78.987.654-3', 'Tecnología Global S.A.', 'adquisiciones@tecglobal.cl', '+56 9 4567 8901', 'ACTIVO', true)
ON CONFLICT (id) DO NOTHING;

SELECT setval('clientes_id_seq', (SELECT MAX(id) FROM clientes));

INSERT INTO productos (id, codigo, nombre, descripcion, precio, stock_disponible, stock_minimo, activo, version) VALUES
(1, 'P-001', 'Cable HDMI 2 m', 'Cable HDMI 2.0 de alta velocidad 4K', 4990.00, 120, 30, true, 0),
(2, 'P-002', 'Mouse inalámbrico', 'Mouse óptico inalámbrico 2.4GHz', 12990.00, 8, 25, true, 0),
(3, 'P-003', 'Teclado mecánico', 'Teclado mecánico RGB switch azul', 39990.00, 45, 20, true, 0),
(4, 'P-004', 'Monitor 24"', 'Monitor IPS 24 pulgadas Full HD 75Hz', 119990.00, 6, 10, true, 0),
(5, 'P-005', 'Notebook 15"', 'Notebook Intel Core i5 16GB RAM 512GB SSD', 489990.00, 14, 5, true, 0),
(6, 'P-006', 'Adaptador USB-C', 'Adaptador multipuerto USB-C a HDMI/USB 3.0', 8990.00, 2, 15, true, 0),
(7, 'P-007', 'Audífonos Bluetooth', 'Audífonos inalámbricos con cancelación de ruido', 24990.00, 18, 20, true, 0),
(8, 'P-008', 'Webcam HD', 'Cámara web 1080p con micrófono estéreo', 29990.00, 60, 15, true, 0)
ON CONFLICT (id) DO NOTHING;

SELECT setval('productos_id_seq', (SELECT MAX(id) FROM productos));

INSERT INTO pedidos (id, numero_pedido, cliente_id, fecha, estado, total, observacion) VALUES
(1, '#1024', 1, '2026-10-09 10:30:00-03', 'PENDIENTE', 303900.00, 'Pedido registrado en estado pendiente'),
(2, '#1019', 1, '2026-10-02 14:15:00-03', 'COMPLETADO', 187450.00, 'Entregado conforme'),
(3, '#1011', 1, '2026-09-24 11:00:00-03', 'ENVIADO', 96980.00, 'Despachado a sucursal'),
(4, '#1004', 1, '2026-09-15 16:40:00-03', 'CANCELADO', 142300.00, 'Cancelado por solicitud del cliente - stock restaurado'),
(5, '#0987', 1, '2026-08-28 09:20:00-03', 'COMPLETADO', 412760.00, 'Venta corporativa cerrada')
ON CONFLICT (id) DO NOTHING;

SELECT setval('pedidos_id_seq', (SELECT MAX(id) FROM pedidos));

INSERT INTO items_pedido (id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 4, 2, 119990.00, 239980.00), 
(2, 1, 3, 1, 39990.00, 39990.00),  
(3, 1, 7, 1, 23930.00, 23930.00)   
ON CONFLICT (id) DO NOTHING;

SELECT setval('items_pedido_id_seq', (SELECT MAX(id) FROM items_pedido));
