-- PisciData inventory test data
-- Safe and idempotent: this script does not update, delete, truncate or drop data.
-- Execute only after reviewing the inspection results below.

USE piscidatadb;

-- 1. Inspect the real inventory schema and current data.
DESCRIBE Feed;
DESCRIBE Supply;

SELECT
	f.Id,
	f.Name,
	f.OwnerUserId,
	u.FirstName,
	u.LastName,
	f.IsActive AS FarmIsActive,
	u.IsActive AS OwnerIsActive
FROM Farm AS f
INNER JOIN User AS u ON u.Id = f.OwnerUserId
WHERE f.IsActive <> 0
  AND u.IsActive <> 0
ORDER BY f.Id;

SELECT
	f.Id AS FarmId,
	f.Name AS FarmName,
	p.Id AS PondId,
	p.Code,
	p.Name AS PondName,
	p.IsActive
FROM Farm AS f
INNER JOIN Pond AS p ON p.FarmId = f.Id
WHERE f.IsActive <> 0
ORDER BY f.Id, p.Id;

SELECT
	Id,
	FarmId,
	Brand,
	ProductName,
	Phase,
	ProteinPercentage,
	PelletSizeMm,
	StockKg,
	MinimumStockKg,
	IsActive
FROM Feed
ORDER BY FarmId, Id;

SELECT
	Id,
	FarmId,
	Name,
	Category,
	Quantity,
	Unit,
	MinimumStock,
	IsActive
FROM Supply
ORDER BY FarmId, Id;

-- 2. Resolve an existing active farm and its existing active owner.
-- No user, farm or pond is created or modified by this script.
SET @FarmId = (
	SELECT f.Id
	FROM Farm AS f
	INNER JOIN User AS u ON u.Id = f.OwnerUserId
	WHERE f.IsActive <> 0
	  AND u.IsActive <> 0
	ORDER BY f.Id
	LIMIT 1
);

SET @OwnerUserId = (
	SELECT f.OwnerUserId
	FROM Farm AS f
	INNER JOIN User AS u ON u.Id = f.OwnerUserId
	WHERE f.Id = @FarmId
	  AND f.IsActive <> 0
	  AND u.IsActive <> 0
	LIMIT 1
);

SELECT
	@FarmId AS SelectedFarmId,
	f.Name AS SelectedFarmName,
	@OwnerUserId AS SelectedOwnerUserId,
	CONCAT(u.FirstName, ' ', u.LastName) AS SelectedOwnerName
FROM Farm AS f
INNER JOIN User AS u ON u.Id = f.OwnerUserId
WHERE f.Id = @FarmId
  AND f.OwnerUserId = @OwnerUserId;

SET @TestMarker = '[PRUEBA INVENTARIO PisciData]';

-- 3. Add four Feed records only when the same test product is not already
-- present in the selected farm. Protein and pellet are represented by the
-- real Feed columns ProteinPercentage and PelletSizeMm.
-- StockKg and MinimumStockKg are both expressed in kilograms.

-- Protein: stock above minimum => DISPONIBLE.
INSERT INTO Feed
	(FarmId, Brand, ProductName, Phase, ProteinPercentage, PelletSizeMm,
	 StockKg, MinimumStockKg, IsActive, UserId)
SELECT
	@FarmId,
	'PisciData Prueba',
	CONCAT('Proteína crecimiento ', @TestMarker),
	'F1',
	40.00,
	NULL,
	120.000,
	50.000,
	TRUE,
	@OwnerUserId
WHERE @FarmId IS NOT NULL
  AND @OwnerUserId IS NOT NULL
  AND NOT EXISTS (
	  SELECT 1
	  FROM Feed
	  WHERE FarmId = @FarmId
		AND ProductName = CONVERT(CONCAT('Proteína crecimiento ', @TestMarker) USING utf8mb4)
			COLLATE utf8mb4_unicode_ci
  );

-- Pellet: stock equal to minimum => BAJO according to backend rules.
INSERT INTO Feed
	(FarmId, Brand, ProductName, Phase, ProteinPercentage, PelletSizeMm,
	 StockKg, MinimumStockKg, IsActive, UserId)
SELECT
	@FarmId,
	'PisciData Prueba',
	CONCAT('Pellet crecimiento ', @TestMarker),
	'F1',
	32.00,
	3.00,
	60.000,
	60.000,
	TRUE,
	@OwnerUserId
WHERE @FarmId IS NOT NULL
  AND @OwnerUserId IS NOT NULL
  AND NOT EXISTS (
	  SELECT 1
	  FROM Feed
	  WHERE FarmId = @FarmId
		AND ProductName = CONVERT(CONCAT('Pellet crecimiento ', @TestMarker) USING utf8mb4)
			COLLATE utf8mb4_unicode_ci
  );

-- Protein: stock below minimum => BAJO.
INSERT INTO Feed
	(FarmId, Brand, ProductName, Phase, ProteinPercentage, PelletSizeMm,
	 StockKg, MinimumStockKg, IsActive, UserId)
SELECT
	@FarmId,
	'PisciData Prueba',
	CONCAT('Proteína engorde ', @TestMarker),
	'F2',
	36.00,
	NULL,
	15.000,
	30.000,
	TRUE,
	@OwnerUserId
WHERE @FarmId IS NOT NULL
  AND @OwnerUserId IS NOT NULL
  AND NOT EXISTS (
	  SELECT 1
	  FROM Feed
	  WHERE FarmId = @FarmId
		AND ProductName = CONVERT(CONCAT('Proteína engorde ', @TestMarker) USING utf8mb4)
			COLLATE utf8mb4_unicode_ci
  );

-- Pellet: stock above minimum => DISPONIBLE.
INSERT INTO Feed
	(FarmId, Brand, ProductName, Phase, ProteinPercentage, PelletSizeMm,
	 StockKg, MinimumStockKg, IsActive, UserId)
SELECT
	@FarmId,
	'PisciData Prueba',
	CONCAT('Pellet engorde ', @TestMarker),
	'F2',
	30.00,
	6.00,
	250.000,
	100.000,
	TRUE,
	@OwnerUserId
WHERE @FarmId IS NOT NULL
  AND @OwnerUserId IS NOT NULL
  AND NOT EXISTS (
	  SELECT 1
	  FROM Feed
	  WHERE FarmId = @FarmId
		AND ProductName = CONVERT(CONCAT('Pellet engorde ', @TestMarker) USING utf8mb4)
			COLLATE utf8mb4_unicode_ci
  );

-- 4. Verification queries. These are read-only.
SELECT
	feed.Id,
	feed.FarmId,
	farm.Name AS FarmName,
	feed.UserId,
	CONCAT(owner.FirstName, ' ', owner.LastName) AS OwnerName,
	feed.Brand,
	feed.ProductName,
	feed.Phase,
	CASE
		WHEN feed.PelletSizeMm IS NOT NULL THEN 'PELLET'
		WHEN feed.ProteinPercentage IS NOT NULL THEN 'PROTEÍNA'
		ELSE 'NO DETERMINADO'
	END AS TestClassification,
	feed.ProteinPercentage,
	feed.PelletSizeMm,
	feed.StockKg,
	feed.MinimumStockKg,
	CASE
		WHEN feed.StockKg IS NULL OR feed.MinimumStockKg IS NULL THEN 'NO DISPONIBLE'
		WHEN feed.StockKg <= feed.MinimumStockKg THEN 'BAJO'
		ELSE 'DISPONIBLE'
	END AS ExpectedStockStatus,
	feed.IsActive
FROM Feed AS feed
INNER JOIN Farm AS farm ON farm.Id = feed.FarmId
INNER JOIN User AS owner ON owner.Id = farm.OwnerUserId
WHERE feed.FarmId = @FarmId
	AND feed.ProductName LIKE CONVERT(CONCAT('%', @TestMarker, '%') USING utf8mb4)
	  COLLATE utf8mb4_unicode_ci
ORDER BY feed.Id;

SELECT
	supply.Id,
	supply.FarmId,
	farm.Name AS FarmName,
	supply.Name,
	supply.Category,
	supply.Quantity,
	supply.Unit,
	supply.MinimumStock,
	supply.IsActive
FROM Supply AS supply
INNER JOIN Farm AS farm ON farm.Id = supply.FarmId
WHERE supply.FarmId = @FarmId
ORDER BY supply.Id;

-- Expected result: four test Feed rows, no existing rows changed, and no
-- Supply rows changed. If @FarmId is NULL, no Feed rows are inserted.
