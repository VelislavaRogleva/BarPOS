INSERT INTO `bar_table` (`id`, `available`, `number`) VALUES
  (1, b'1', 1),
  (2, b'1', 2),
  (3, b'1', 3);

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`) VALUES
  (5, 'alcohol'),
  (2, 'beers'),
  (4, 'cocktails'),
  (3, 'nuts'),
  (1, 'wines');

-- --------------------------------------------------------
--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `available`, `barcode`, `description`, `image_path`, `name`, `price`, `category_id`) VALUES
  (2, b'1', '1234', 'Very strong alcohol', NULL, 'Aftershock', 39.99, 5),
  (3, b'1', '2345', 'Red wine', NULL, 'Red wine', 12, 1),
  (4, b'1', '3456', 'Bulgarian beer', NULL, 'Zagorka', 1.59, 2);

-- --------------------------------------------------------
--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `role`) VALUES
  (1, 'ADMIN'),
  (2, 'USER');

-- --------------------------------------------------------
--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `password_hash`, `role_id`, `is_active`) VALUES
  (1, 'Ivan', '$2a$04$v1KhIqXZmMqC0n3NpGe2l.dSkaITGBkIBwXBvyhdqzHpfWmRFeikC', 1, 1),
  (2, 'Pesho', '$2a$04$v1KhIqXZmMqC0n3NpGe2l.dSkaITGBkIBwXBvyhdqzHpfWmRFeikC', 2, 1),
  (3, 'Gosho', '$2a$04$v1KhIqXZmMqC0n3NpGe2l.dSkaITGBkIBwXBvyhdqzHpfWmRFeikC', 2, 1);

