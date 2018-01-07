SELECT p.name, p.available, p.cost, p.price, o.status,(p.cost - p.price) AS profit, SUM(op.product_quantity) AS sold
FROM orders AS o
INNER JOIN order_products AS op
    ON op.order_id = o.id
INNER JOIN products p
    ON op.product_id = p.id
WHERE o.date >= date("2011-12-01")
      AND o.date <= date("2020-12-01")
      AND o.status LIKE 'closed'
GROUP BY p.id
ORDER BY sold DESC