
Usa esta sentencia sql como una referencia para la base de datos, no como un script para hacer correr
-- ============================================================
-- FISH FARM DATABASE
-- ============================================================

CREATE DATABASE PisciDataDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE PisciDataDB;


-- ============================================================
-- 1. USER
-- ============================================================

CREATE TABLE User (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    FirstName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NOT NULL,
    Phone VARCHAR(30) NOT NULL,

    PasswordHash VARCHAR(255) NOT NULL,

    Role VARCHAR(30) NOT NULL DEFAULT 'Owner',

    LastLoginAt DATETIME NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,

    -- Last user who modified this user
    UserId INT NULL,

    CONSTRAINT FK_User_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 2. FARM
-- ============================================================

CREATE TABLE Farm (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    OwnerUserId INT NOT NULL,

    Name VARCHAR(150) NOT NULL,
    Description VARCHAR(500) NULL,

    Address VARCHAR(255) NULL,
    Latitude DECIMAL(10, 7) NULL,
    Longitude DECIMAL(10, 7) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_Farm_OwnerUser
        FOREIGN KEY (OwnerUserId)
        REFERENCES User(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Farm_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 3. POND
-- ============================================================

CREATE TABLE Pond (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    FarmId INT NOT NULL,

    Code VARCHAR(50) NOT NULL,
    Name VARCHAR(100) NULL,

    Shape VARCHAR(50) NULL,

    Width DECIMAL(10, 2) NULL,
    Length DECIMAL(10, 2) NULL,
    Diameter DECIMAL(10, 2) NULL,
    Depth DECIMAL(10, 2) NULL,
    Area DECIMAL(12, 2) NULL,

    Description VARCHAR(500) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT UQ_Pond_Farm_Code
        UNIQUE (FarmId, Code),

    CONSTRAINT FK_Pond_Farm
        FOREIGN KEY (FarmId)
        REFERENCES Farm(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Pond_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 4. SPECIES
-- ============================================================

CREATE TABLE Species (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    CommonName VARCHAR(100) NOT NULL,
    ScientificName VARCHAR(150) NULL,
    Description VARCHAR(500) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT UQ_Species_CommonName
        UNIQUE (CommonName),

    CONSTRAINT FK_Species_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 5. PRODUCTION CYCLE
-- ============================================================

CREATE TABLE ProductionCycle (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    PondId INT NOT NULL,
    SpeciesId INT NOT NULL,

    StartDate DATE NOT NULL,
    EndDate DATE NULL,

    InitialFishCount INT NULL,
    InitialAverageWeightGrams DECIMAL(7, 2) NULL,
    InitialAgeDays INT NULL,
    Observations VARCHAR(1000) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_ProductionCycle_Pond
        FOREIGN KEY (PondId)
        REFERENCES Pond(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_ProductionCycle_Species
        FOREIGN KEY (SpeciesId)
        REFERENCES Species(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_ProductionCycle_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 6. FEED
-- ============================================================

CREATE TABLE Feed (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    FarmId INT NOT NULL,

    Brand VARCHAR(100) NOT NULL,
    ProductName VARCHAR(150) NULL,

    Phase VARCHAR(10) NULL,

    ProteinPercentage DECIMAL(5, 2) NULL,
    PelletSizeMm DECIMAL(6, 2) NULL,

    StockKg DECIMAL(12, 3) NULL,
    MinimumStockKg DECIMAL(12, 3) NULL,
    
    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_Feed_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
        
	CONSTRAINT FK_Feed_Farm
		FOREIGN KEY (FarmId)
		REFERENCES Farm(Id)
		ON DELETE RESTRICT
		ON UPDATE CASCADE
		
);


-- ============================================================
-- 7. FEEDING BY WEIGHT
-- ============================================================
-- ============================================================
-- 7. FEEDING BY WEIGHT  (CHANGED)
-- Changes: pellet size and protein are now min/max ranges,
-- added FeedForm and Source.
-- ============================================================

CREATE TABLE FeedingByWeight (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    SpeciesId INT NULL,                         -- NULL = applies to all species

    MinimumWeightGrams DECIMAL(10, 2) NOT NULL,
    MaximumWeightGrams DECIMAL(10, 2) NOT NULL,

    FeedingRatePercentage DECIMAL(6, 3) NULL,   -- % of live weight (biomass) per day
    FeedPhase VARCHAR(10) NULL,                 -- F0, F1, F2, F3 (brand label)

    FeedForm VARCHAR(20) NULL,                  -- POWDER, PELLET, EXTRUDED

    ProteinMinPercentage DECIMAL(5, 2) NULL,
    ProteinMaxPercentage DECIMAL(5, 2) NULL,

    PelletSizeMinMm DECIMAL(6, 2) NULL,         -- NULL for powder
    PelletSizeMaxMm DECIMAL(6, 2) NULL,

    DailyMeals INT NULL,

    Source VARCHAR(255) NULL,                   -- e.g. 'Manual Pacú y Tambaquí 2024, p.37'

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_FeedingByWeight_Species
        FOREIGN KEY (SpeciesId)
        REFERENCES Species(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT FK_FeedingByWeight_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT CK_FeedingByWeight_WeightRange
        CHECK (MaximumWeightGrams > MinimumWeightGrams),

    CONSTRAINT CK_FeedingByWeight_Protein
        CHECK (ProteinMaxPercentage IS NULL
               OR ProteinMinPercentage IS NULL
               OR ProteinMaxPercentage >= ProteinMinPercentage),

    CONSTRAINT CK_FeedingByWeight_Pellet
        CHECK (PelletSizeMaxMm IS NULL
               OR PelletSizeMinMm IS NULL
               OR PelletSizeMaxMm >= PelletSizeMinMm),

    CONSTRAINT CK_FeedingByWeight_Form
        CHECK (FeedForm IS NULL OR FeedForm IN ('POWDER', 'PELLET', 'EXTRUDED'))
);


-- ============================================================
-- 8. FEEDING BY AGE  (CHANGED)
-- Changes: pellet size and protein are now min/max ranges,
-- added ReferenceFishCount (DailyAmountKilo is for this many fish),
-- added FeedPhase, FeedForm and Source.
-- ============================================================

CREATE TABLE FeedingByAge (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    SpeciesId INT NULL,                         -- NULL = applies to all species

    MinimumAgeDays INT NOT NULL,
    MaximumAgeDays INT NOT NULL,

    ApproximatedFishWeightGrams DECIMAL(7, 2) NULL,
    FeedingRatePercentage DECIMAL(6, 3) NULL,

    FeedPhase VARCHAR(10) NULL,
    FeedForm VARCHAR(20) NULL,                  -- POWDER, PELLET, EXTRUDED

    ProteinMinPercentage DECIMAL(5, 2) NULL,
    ProteinMaxPercentage DECIMAL(5, 2) NULL,

    PelletSizeMinMm DECIMAL(6, 2) NULL,         -- NULL for powder
    PelletSizeMaxMm DECIMAL(6, 2) NULL,

    -- DailyAmountKilo is valid for ReferenceFishCount fish.
    -- Scale it: recommendedKg = DailyAmountKilo * (currentFish / ReferenceFishCount)
    ReferenceFishCount INT NOT NULL DEFAULT 1000,
    DailyAmountKilo DECIMAL(6, 2) NULL,

    DailyMeals INT NULL,

    Source VARCHAR(255) NULL,                   -- e.g. 'Manual Pacú y Tambaquí 2024, p.40'

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_FeedingByAge_Species
        FOREIGN KEY (SpeciesId)
        REFERENCES Species(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT FK_FeedingByAge_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT CK_FeedingByAge_AgeRange
        CHECK (MaximumAgeDays >= MinimumAgeDays),

    CONSTRAINT CK_FeedingByAge_ReferenceFish
        CHECK (ReferenceFishCount > 0),

    CONSTRAINT CK_FeedingByAge_Protein
        CHECK (ProteinMaxPercentage IS NULL
               OR ProteinMinPercentage IS NULL
               OR ProteinMaxPercentage >= ProteinMinPercentage),

    CONSTRAINT CK_FeedingByAge_Pellet
        CHECK (PelletSizeMaxMm IS NULL
               OR PelletSizeMinMm IS NULL
               OR PelletSizeMaxMm >= PelletSizeMinMm),

    CONSTRAINT CK_FeedingByAge_Form
        CHECK (FeedForm IS NULL OR FeedForm IN ('POWDER', 'PELLET', 'EXTRUDED'))
);


-- ============================================================
-- 20. WATER QUALITY REFERENCE  (NEW)
-- One row per parameter (and optionally per species).
-- Lets the backend/AI classify a reading as OPTIMAL / WARNING / CRITICAL.
-- ============================================================

CREATE TABLE WaterQualityReference (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    SpeciesId INT NULL,                         -- NULL = applies to all species

    Parameter VARCHAR(30) NOT NULL,             -- DISSOLVED_OXYGEN, PH, TEMPERATURE, TRANSPARENCY
    Unit VARCHAR(20) NOT NULL,                  -- mg/L, pH, C, cm

    OptimalMin DECIMAL(8, 2) NULL,
    OptimalMax DECIMAL(8, 2) NULL,

    WarningMin DECIMAL(8, 2) NULL,              -- below OptimalMin but still tolerable
    WarningMax DECIMAL(8, 2) NULL,              -- above OptimalMax but still tolerable

    CriticalMin DECIMAL(8, 2) NULL,             -- at or below this: risk of mortality
    CriticalMax DECIMAL(8, 2) NULL,             -- at or above this: risk of mortality

    RecommendedAction VARCHAR(500) NULL,        -- text the app/AI can show when out of range
    Source VARCHAR(255) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_WaterQualityReference_Species
        FOREIGN KEY (SpeciesId)
        REFERENCES Species(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT FK_WaterQualityReference_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT CK_WaterQualityReference_Parameter
        CHECK (Parameter IN ('DISSOLVED_OXYGEN', 'PH', 'TEMPERATURE', 'TRANSPARENCY')),

    CONSTRAINT CK_WaterQualityReference_Optimal
        CHECK (OptimalMin IS NULL OR OptimalMax IS NULL OR OptimalMax >= OptimalMin)
);

CREATE INDEX IX_WaterQualityReference_Parameter
    ON WaterQualityReference(Parameter, SpeciesId);


-- ============================================================
-- 9. FEEDING
-- ============================================================

CREATE TABLE Feeding (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    ProductionCycleId INT NOT NULL,
    FeedId INT NOT NULL,

    FeedingDate DATE NOT NULL,
    FeedingTime TIME NOT NULL,

    QuantityKg DECIMAL(12, 3) NOT NULL,

    MealNumber INT NULL,

    Behavior VARCHAR(255) NULL,
    Observations VARCHAR(1000) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_Feeding_ProductionCycle
        FOREIGN KEY (ProductionCycleId)
        REFERENCES ProductionCycle(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Feeding_Feed
        FOREIGN KEY (FeedId)
        REFERENCES Feed(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Feeding_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 10. WATER QUALITY
-- ============================================================

CREATE TABLE WaterQuality (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    ProductionCycleId INT NOT NULL,

    MeasurementDate DATE NOT NULL,
    MeasurementTime TIME NOT NULL,

    TransparencyCm DECIMAL(8, 2) NULL,
    TemperatureC DECIMAL(6, 2) NULL,
    PH DECIMAL(5, 2) NULL,
    DissolvedOxygen DECIMAL(8, 3) NULL,

    Observations VARCHAR(1000) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_WaterQuality_ProductionCycle
        FOREIGN KEY (ProductionCycleId)
        REFERENCES ProductionCycle(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_WaterQuality_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 11. BIOMETRICS
-- ============================================================

CREATE TABLE Biometrics (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    ProductionCycleId INT NOT NULL,

    MeasurementDate DATE NOT NULL,

    AverageWeightGrams DECIMAL(10, 2) NULL,
    BiomassKg DECIMAL(12, 3) NULL,
    CalculatedFeedKg DECIMAL(12, 3) NULL,

    HealthStatus VARCHAR(100) NULL,
    Observations VARCHAR(1000) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_Biometrics_ProductionCycle
        FOREIGN KEY (ProductionCycleId)
        REFERENCES ProductionCycle(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Biometrics_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 12. BIOMETRIC SAMPLE
-- ============================================================

CREATE TABLE BiometricsSample (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    BiometricsId INT NOT NULL,

    SampleNumber INT NOT NULL,

    BucketWaterWeightKg DECIMAL(10, 3) NULL,
    BucketWithFishWeightKg DECIMAL(10, 3) NULL,

    FishCount INT NOT NULL,

    TotalFishWeightKg DECIMAL(10, 3) NULL,
    AverageWeightGrams DECIMAL(10, 2) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_BiometricsSample_Biometrics
        FOREIGN KEY (BiometricsId)
        REFERENCES Biometrics(Id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT FK_BiometricsSample_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 13. MORTALITY
-- ============================================================

CREATE TABLE Mortality (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    ProductionCycleId INT NOT NULL,

    MortalityDate DATE NOT NULL,

    DeadFishCount INT NOT NULL,

    Cause VARCHAR(255) NULL,
    ObservedSigns VARCHAR(1000) NULL,
    Observations VARCHAR(1000) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_Mortality_ProductionCycle
        FOREIGN KEY (ProductionCycleId)
        REFERENCES ProductionCycle(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Mortality_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 14. HARVEST
-- ============================================================

CREATE TABLE Harvest (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    ProductionCycleId INT NOT NULL,

    HarvestDate DATE NOT NULL,

    FishCount INT NOT NULL,
    TotalWeightKg DECIMAL(12, 3) NULL,
    AverageWeightGrams DECIMAL(12, 3) NULL,

    HarvestType VARCHAR(100) NULL,

    Observations VARCHAR(1000) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_Harvest_ProductionCycle
        FOREIGN KEY (ProductionCycleId)
        REFERENCES ProductionCycle(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Harvest_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 15. SUPPLY
-- ============================================================

CREATE TABLE Supply (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    FarmId INT NOT NULL,

    Name VARCHAR(150) NOT NULL,
    Category VARCHAR(100) NULL,

    Quantity DECIMAL(12, 3) NOT NULL,
    Unit VARCHAR(30) NOT NULL,

    MinimumStock DECIMAL(12, 3) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_Supply_Farm
        FOREIGN KEY (FarmId)
        REFERENCES Farm(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Supply_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 16. SUPPLY MOVEMENT
-- ============================================================

CREATE TABLE SupplyMovement (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    SupplyId INT NOT NULL,

    MovementDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    MovementType VARCHAR(30) NOT NULL,

    Quantity DECIMAL(12, 3) NOT NULL,

    Reason VARCHAR(255) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_SupplyMovement_Supply
        FOREIGN KEY (SupplyId)
        REFERENCES Supply(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_SupplyMovement_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT CK_SupplyMovement_Type
        CHECK (MovementType IN ('IN', 'OUT', 'ADJUSTMENT'))
);


-- ============================================================
-- 17. TASK
-- ============================================================

CREATE TABLE Task (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    FarmId INT NOT NULL,
    PondId INT NULL,

    Title VARCHAR(200) NOT NULL,
    Description VARCHAR(1000) NULL,

    ScheduledDate DATETIME NULL,
    CompletedDate DATETIME NULL,

    Priority VARCHAR(30) NOT NULL DEFAULT 'Normal',

    Completed BOOLEAN NOT NULL DEFAULT FALSE,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_Task_Farm
        FOREIGN KEY (FarmId)
        REFERENCES Farm(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Task_Pond
        FOREIGN KEY (PondId)
        REFERENCES Pond(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT FK_Task_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 18. AI CONVERSATION
-- ============================================================

CREATE TABLE AIConversation (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    OwnerUserId INT NOT NULL,

    StartDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LastMessageDate DATETIME NULL,

    Title VARCHAR(255) NULL,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_AIConversation_OwnerUser
        FOREIGN KEY (OwnerUserId)
        REFERENCES User(Id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_AIConversation_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);


-- ============================================================
-- 19. AI MESSAGE
-- ============================================================

CREATE TABLE AIMessage (
    Id INT AUTO_INCREMENT PRIMARY KEY,

    AIConversationId INT NOT NULL,

    Role VARCHAR(30) NOT NULL,
    Content TEXT NULL, 
    Transcription TEXT NULL,  
    MessageType VARCHAR(30) NOT NULL DEFAULT 'TEXT',

    MessageDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Audit
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    IsActive BOOLEAN NOT NULL DEFAULT TRUE,
    UserId INT NULL,

    CONSTRAINT FK_AIMessage_Conversation
        FOREIGN KEY (AIConversationId)
        REFERENCES AIConversation(Id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT FK_AIMessage_User
        FOREIGN KEY (UserId)
        REFERENCES User(Id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT CK_AIMessage_Role
        CHECK (Role IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')),

    CONSTRAINT CK_AIMessage_Type
        CHECK (MessageType IN ('TEXT', 'VOICE', 'SYSTEM', 'TOOL_RESULT'))
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX IX_Farm_OwnerUserId
    ON Farm(OwnerUserId);

CREATE INDEX IX_Farm_UserId
    ON Farm(UserId);

CREATE INDEX IX_Pond_FarmId
    ON Pond(FarmId);

CREATE INDEX IX_Pond_UserId
    ON Pond(UserId);

CREATE INDEX IX_ProductionCycle_PondId
    ON ProductionCycle(PondId);

CREATE INDEX IX_ProductionCycle_SpeciesId
    ON ProductionCycle(SpeciesId);

CREATE INDEX IX_ProductionCycle_UserId
    ON ProductionCycle(UserId);

CREATE INDEX IX_Feeding_ProductionCycleId
    ON Feeding(ProductionCycleId);

CREATE INDEX IX_Feeding_FeedId
    ON Feeding(FeedId);

CREATE INDEX IX_WaterQuality_ProductionCycleId
    ON WaterQuality(ProductionCycleId);

CREATE INDEX IX_Biometrics_ProductionCycleId
    ON Biometrics(ProductionCycleId);

CREATE INDEX IX_BiometricsSample_BiometricsId
    ON BiometricsSample(BiometricsId);

CREATE INDEX IX_Mortality_ProductionCycleId
    ON Mortality(ProductionCycleId);

CREATE INDEX IX_Harvest_ProductionCycleId
    ON Harvest(ProductionCycleId);

CREATE INDEX IX_Supply_FarmId
    ON Supply(FarmId);

CREATE INDEX IX_SupplyMovement_SupplyId
    ON SupplyMovement(SupplyId);

CREATE INDEX IX_Task_FarmId
    ON Task(FarmId);

CREATE INDEX IX_Task_PondId
    ON Task(PondId);

CREATE INDEX IX_AIConversation_OwnerUserId
    ON AIConversation(OwnerUserId);

CREATE INDEX IX_AIMessage_AIConversationId
    ON AIMessage(AIConversationId);

CREATE INDEX IX_Feed_FarmId
    ON Feed(FarmId);




USE PisciDataDB;

-- ============================================================
-- SAMPLE DATA: User, Farm, Pond, Species, Supply,
--              ProductionCycle, WaterQuality, Task
-- Run this BEFORE tambaqui_feeding_data.sql (or after, both work,
-- since each script creates Tambaqui if it is missing).
-- IDs are looked up with variables, so it works even if the
-- tables already contain other rows.
-- ============================================================


-- ============================================================
-- 1. USERS
-- NOTE: PasswordHash values are placeholders. Replace them with
-- real hashes generated by your application (e.g. bcrypt).
-- ============================================================
INSERT INTO User (FirstName, LastName, Phone, PasswordHash, Role, LastLoginAt) VALUES
    ('Carlos', 'Mendoza', '70123456', '$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi', 'Owner',         '2026-10-07 18:30:00'),
    ('Lucia',  'Rojas',   '71234567', '$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi', 'Administrator', '2026-10-08 07:45:00'),
    ('Miguel', 'Flores',  '72345678', '$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi', 'Technician',    '2026-10-08 06:50:00'),
    ('Juan',   'Quispe',  '73456789', '$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi', 'Worker',        '2026-10-08 06:10:00'),
    ('Ana',    'Vargas',  '74567890', '$2a$11$99fgnHla7u8W8HI56nlMqemWOjbtWDCW25A.Rz5sWT9ZGE81ofzYi', 'Owner',         '2026-10-06 20:15:00');

SET @Carlos = (SELECT Id FROM User WHERE Phone = '+59170123456');
SET @Lucia  = (SELECT Id FROM User WHERE Phone = '+59171234567');
SET @Miguel = (SELECT Id FROM User WHERE Phone = '+59172345678');
SET @Juan   = (SELECT Id FROM User WHERE Phone = '+59173456789');
SET @Ana    = (SELECT Id FROM User WHERE Phone = '+59174567890');

-- Last modifier of each user (self-reference)
UPDATE User SET UserId = @Carlos WHERE Id IN (@Carlos, @Lucia, @Miguel, @Juan);
UPDATE User SET UserId = @Ana    WHERE Id = @Ana;


-- ============================================================
-- 2. FARMS
-- ============================================================
INSERT INTO Farm (OwnerUserId, Name, Description, Address, Latitude, Longitude, UserId) VALUES
    (@Carlos, 'Piscicola El Paraiso',
     'Tambaqui and pacu grow-out farm with three earthen ponds.',
     'Km 45 Carretera al Chapare, Villa Tunari, Cochabamba',
     -16.9742000, -65.4209000, @Carlos),
    (@Ana, 'Granja Los Tambaquies',
     'Family-run farm focused on tambaqui production for local markets.',
     'Comunidad Chimore, Carrasco, Cochabamba',
     -16.9906000, -65.1462000, @Ana);

SET @Farm1 = (SELECT Id FROM Farm WHERE Name = 'Piscicola El Paraiso'   AND OwnerUserId = @Carlos);
SET @Farm2 = (SELECT Id FROM Farm WHERE Name = 'Granja Los Tambaquies'  AND OwnerUserId = @Ana);


-- ============================================================
-- 3. PONDS
-- Area: rectangular = Width x Length, circular = PI x (Diameter/2)^2
-- ============================================================
INSERT INTO Pond (FarmId, Code, Name, Shape, Width, Length, Diameter, Depth, Area, Description, UserId) VALUES
    (@Farm1, 'P-01', 'Main Pond',    'Rectangular', 20.00, 30.00, NULL,  1.50, 600.00, 'Largest pond, aerator installed.',          @Carlos),
    (@Farm1, 'P-02', 'Nursery Pond', 'Rectangular', 15.00, 25.00, NULL,  1.20, 375.00, 'Used for juveniles.',                       @Carlos),
    (@Farm1, 'P-03', 'Round Pond',   'Circular',    NULL,  NULL,  12.00, 1.40, 113.10, 'Geomembrane-lined circular tank.',           @Carlos),
    (@Farm2, 'P-01', 'Pond A',       'Rectangular', 20.00, 20.00, NULL,  1.60, 400.00, 'Supplied by gravity from the river channel.', @Ana),
    (@Farm2, 'P-02', 'Pond B',       'Circular',    NULL,  NULL,  10.00, 1.30,  78.54, 'Small pond for sorting and quarantine.',     @Ana);

SET @F1P01 = (SELECT Id FROM Pond WHERE FarmId = @Farm1 AND Code = 'P-01');
SET @F1P02 = (SELECT Id FROM Pond WHERE FarmId = @Farm1 AND Code = 'P-02');
SET @F1P03 = (SELECT Id FROM Pond WHERE FarmId = @Farm1 AND Code = 'P-03');
SET @F2P01 = (SELECT Id FROM Pond WHERE FarmId = @Farm2 AND Code = 'P-01');
SET @F2P02 = (SELECT Id FROM Pond WHERE FarmId = @Farm2 AND Code = 'P-02');


-- ============================================================
-- 4. SPECIES (needed by ProductionCycle)
-- ============================================================
INSERT INTO Species (CommonName, ScientificName, Description, UserId)
VALUES ('Tambaqui', 'Colossoma macropomum', 'Large Amazonian freshwater fish, also known as cachama or gamitana.', @Carlos)
ON DUPLICATE KEY UPDATE ScientificName = VALUES(ScientificName);

SET @Tambaqui = (SELECT Id FROM Species WHERE CommonName = 'Tambaqui');


-- ============================================================
-- 5. SUPPLIES
-- ============================================================
INSERT INTO Supply (FarmId, Name, Category, Quantity, Unit, MinimumStock, UserId) VALUES
    (@Farm1, 'Agricultural lime',        'Water treatment', 250.000, 'kg',    100.000, @Lucia),
    (@Farm1, 'Common salt',              'Water treatment', 120.000, 'kg',     50.000, @Lucia),
    (@Farm1, 'Oxytetracycline',          'Medicine',          3.000, 'kg',      1.000, @Miguel),
    (@Farm1, 'Harvest net 50 m',         'Equipment',         2.000, 'units',   1.000, @Lucia),
    (@Farm1, 'Diesel for aerator pump',  'Fuel',             80.000, 'L',      40.000, @Juan),
    (@Farm1, 'Plastic buckets 20 L',     'Equipment',         6.000, 'units',   4.000, @Juan),
    (@Farm2, 'Agricultural lime',        'Water treatment', 140.000, 'kg',     60.000, @Ana),
    (@Farm2, 'Common salt',              'Water treatment',  35.000, 'kg',     40.000, @Ana),
    (@Farm2, 'Dissolved oxygen probe',   'Equipment',         1.000, 'units',   1.000, @Ana),
    (@Farm2, 'Gasoline for water pump',  'Fuel',             25.000, 'L',      20.000, @Ana);


-- ============================================================
-- 6. PRODUCTION CYCLES
-- ============================================================
INSERT INTO ProductionCycle
    (PondId, SpeciesId, StartDate, EndDate, InitialFishCount, InitialAverageWeightGrams, InitialAgeDays, Observations, UserId)
VALUES
    (@F1P01, @Tambaqui, '2026-03-02', NULL,         5000, 25.00,  90, 'Fingerlings from local hatchery. Cycle in progress.',        @Miguel),
    (@F1P02, @Tambaqui, '2026-08-15', NULL,         3000,  5.00,  45, 'Juveniles on high-protein feed.',                            @Miguel),
    (@F1P03, @Tambaqui, '2025-09-01', '2026-02-20', 2000, 10.00,  60, 'Completed cycle, harvested after 172 days.',                 @Miguel),
    (@F2P01, @Tambaqui, '2026-06-10', NULL,         4000, 15.00,  70, 'Stocked at 10 fish per m2. Cycle in progress.',              @Ana),
    (@F2P02, @Tambaqui, '2026-09-20', NULL,         1000,  3.00,  35, 'Small batch for restocking and testing.',                    @Ana);

SET @C1 = (SELECT Id FROM ProductionCycle WHERE PondId = @F1P01 AND StartDate = '2026-03-02');
SET @C2 = (SELECT Id FROM ProductionCycle WHERE PondId = @F1P02 AND StartDate = '2026-08-15');
SET @C3 = (SELECT Id FROM ProductionCycle WHERE PondId = @F1P03 AND StartDate = '2025-09-01');
SET @C4 = (SELECT Id FROM ProductionCycle WHERE PondId = @F2P01 AND StartDate = '2026-06-10');
SET @C5 = (SELECT Id FROM ProductionCycle WHERE PondId = @F2P02 AND StartDate = '2026-09-20');


-- ============================================================
-- 7. WATER QUALITY
-- Transparency in cm, temperature in C, dissolved oxygen in mg/L
-- ============================================================
INSERT INTO WaterQuality
    (ProductionCycleId, MeasurementDate, MeasurementTime, TransparencyCm, TemperatureC, PH, DissolvedOxygen, Observations, UserId)
VALUES
    -- Cycle 1: Farm 1, P-01
    (@C1, '2026-10-05', '06:00:00', 35.00, 25.80, 7.10, 4.200, 'Normal early-morning values.',                @Juan),
    (@C1, '2026-10-05', '16:00:00', 33.00, 28.90, 7.60, 7.100, NULL,                                          @Juan),
    (@C1, '2026-10-06', '06:00:00', 34.00, 25.60, 7.00, 3.900, 'Aerator turned on at 05:00.',                 @Juan),
    (@C1, '2026-10-07', '06:00:00', 30.00, 26.10, 6.90, 3.400, 'Slightly low oxygen, extended aeration.',     @Juan),
    (@C1, '2026-10-07', '16:00:00', 28.00, 29.30, 7.50, 6.800, NULL,                                          @Juan),
    -- Cycle 2: Farm 1, P-02
    (@C2, '2026-10-06', '06:30:00', 40.00, 26.40, 7.30, 5.100, 'Clear water, good plankton bloom.',           @Miguel),
    (@C2, '2026-10-07', '06:30:00', 38.00, 26.20, 7.20, 4.800, NULL,                                          @Miguel),
    (@C2, '2026-10-08', '06:30:00', 37.00, 26.50, 7.20, 4.700, NULL,                                          @Miguel),
    -- Cycle 4: Farm 2, P-01
    (@C4, '2026-10-06', '07:00:00', 32.00, 27.00, 7.40, 4.500, NULL,                                          @Ana),
    (@C4, '2026-10-07', '07:00:00', 31.00, 27.30, 7.50, 4.300, NULL,                                          @Ana),
    (@C4, '2026-10-08', '07:00:00', 25.00, 27.80, 8.30, 3.100, 'High pH and low oxygen, possible algae bloom.', @Ana),
    -- Cycle 5: Farm 2, P-02
    (@C5, '2026-10-07', '07:30:00', 45.00, 26.90, 7.00, 5.400, 'New batch adapting well.',                    @Ana),
    (@C5, '2026-10-08', '07:30:00', 44.00, 27.10, 7.10, 5.200, NULL,                                          @Ana);


-- ============================================================
-- 8. TASKS
-- Priority values used: Low, Normal, High, Urgent
-- ============================================================
INSERT INTO Task
    (FarmId, PondId, Title, Description, ScheduledDate, CompletedDate, Priority, Completed, UserId)
VALUES
    (@Farm1, @F1P01, 'Biometric sampling P-01',        'Take 3 samples of 30 fish each and record average weight.',          '2026-10-10 08:00:00', NULL,                  'High',   FALSE, @Lucia),
    (@Farm1, @F1P01, 'Check aerator',                  'Inspect motor and paddles, change oil if needed.',                   '2026-10-09 07:00:00', NULL,                  'Urgent', FALSE, @Lucia),
    (@Farm1, @F1P02, 'Apply agricultural lime',        'Apply 10 kg of lime along the pond edges.',                          '2026-10-12 09:00:00', NULL,                  'Normal', FALSE, @Miguel),
    (@Farm1, NULL,   'Order feed for next month',      'Request quotes from two suppliers and place the order.',             '2026-10-15 10:00:00', NULL,                  'Normal', FALSE, @Lucia),
    (@Farm1, @F1P03, 'Clean and dry pond P-03',        'Pond is empty after harvest. Clean liner and let it dry.',           '2026-02-25 08:00:00', '2026-02-27 16:30:00', 'Low',    TRUE,  @Juan),
    (@Farm1, NULL,   'Refill diesel for pump',         'Buy 40 L of diesel and refill the tank.',                            '2026-10-04 09:00:00', '2026-10-04 11:20:00', 'Normal', TRUE,  @Juan),
    (@Farm2, @F2P01, 'Treat algae bloom in Pond A',    'pH reached 8.3. Reduce feeding, increase water exchange, retest.',   '2026-10-08 15:00:00', NULL,                  'Urgent', FALSE, @Ana),
    (@Farm2, @F2P02, 'Weekly weight check',            'Weigh 20 juveniles and update the feeding table.',                   '2026-10-13 08:00:00', NULL,                  'Normal', FALSE, @Ana),
    (@Farm2, NULL,   'Buy salt',                       'Stock is below minimum. Buy at least 50 kg.',                        '2026-10-11 10:00:00', NULL,                  'High',   FALSE, @Ana),
    (@Farm2, @F2P01, 'Calibrate oxygen probe',         'Calibrate the probe with the standard solution.',                    '2026-10-01 07:30:00', '2026-10-01 08:10:00', 'Low',    TRUE,  @Ana);
    
    
    
    
    
    
    
    
-- ============================================================
-- FeedingByWeight  (manual p.37)
-- ============================================================
INSERT INTO FeedingByWeight
 (SpeciesId, MinimumWeightGrams, MaximumWeightGrams, FeedingRatePercentage, FeedPhase, FeedForm,
  ProteinMinPercentage, ProteinMaxPercentage, PelletSizeMinMm, PelletSizeMaxMm, DailyMeals, Source)
VALUES
 (1,    0,    5, 15.000, 'F0', 'POWDER', 45, 55, NULL, NULL, 4, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,    5,   10, 10.000, 'F1', 'POWDER', 40, 40, NULL, NULL, 3, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,   10,   50,  7.000, 'F1', NULL,     40, 40, 2,    4,    3, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,   50,  100,  5.000, 'F1', NULL,     40, 40, 2,    4,    3, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,  100,  200,  4.000, 'F2', NULL,     30, 30, 4,    6,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,  200,  300,  3.000, 'F2', NULL,     30, 30, 4,    6,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,  300,  400,  2.500, 'F2', NULL,     30, 30, 4,    6,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,  400,  500,  2.000, 'F3', NULL,     28, 28, 6,   10,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,  500,  600,  1.500, 'F3', NULL,     28, 28, 6,   10,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,  600,  700,  1.200, 'F3', NULL,     28, 28, 6,   10,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,  700,  800,  1.000, 'F3', NULL,     28, 28, 6,   10,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1,  800, 1200,  0.600, 'F3', NULL,     28, 28, 6,   10,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37'),
 (1, 1200, 2000,  0.600, 'F3', NULL,     28, 28, 6,   10,    2, 'Manual Pacú y Tambaquí 5ta ed., p.37');


-- ============================================================
-- FeedingByAge  (manual p.40, table for 1000 fish)
-- ============================================================
INSERT INTO FeedingByAge
 (SpeciesId, MinimumAgeDays, MaximumAgeDays, ApproximatedFishWeightGrams, FeedingRatePercentage,
  FeedPhase, FeedForm, ProteinMinPercentage, ProteinMaxPercentage, PelletSizeMinMm, PelletSizeMaxMm,
  ReferenceFishCount, DailyAmountKilo, DailyMeals, Source)
VALUES
 (1,   0,   6,    0.50, NULL, NULL, 'POWDER', 55, 55, NULL, NULL, 1000,  0.10, 6, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,   7,  13,    3.00, NULL, NULL, 'POWDER', 55, 55, NULL, NULL, 1000,  0.50, 6, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  14,  20,    7.00, NULL, NULL, 'POWDER', 55, 55, NULL, NULL, 1000,  0.70, 6, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  21,  27,   13.00, NULL, NULL, NULL,     40, 40, 1,    2,    1000,  1.00, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  28,  34,   20.00, NULL, NULL, NULL,     40, 40, 1,    2,    1000,  1.40, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  35,  41,   25.00, NULL, NULL, NULL,     40, 40, 1,    2,    1000,  1.60, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  42,  48,   32.00, NULL, NULL, NULL,     40, 40, 2,    4,    1000,  1.90, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  49,  55,   41.00, NULL, NULL, NULL,     40, 40, 2,    4,    1000,  2.10, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  56,  62,   54.00, NULL, NULL, NULL,     40, 40, 2,    4,    1000,  2.70, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  63,  69,   69.00, NULL, NULL, NULL,     40, 40, 4,    6,    1000,  3.20, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  70,  76,   85.00, NULL, NULL, NULL,     32, 32, 4,    6,    1000,  3.60, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  77,  83,  107.00, NULL, NULL, NULL,     32, 32, 4,    6,    1000,  4.00, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  84,  90,  132.00, NULL, NULL, NULL,     32, 32, 4,    6,    1000,  4.40, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  91,  97,  158.00, NULL, NULL, NULL,     32, 32, 4,    6,    1000,  4.70, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1,  98, 104,  188.00, NULL, NULL, NULL,     32, 32, 4,    6,    1000,  5.10, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 105, 111,  220.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  5.70, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 112, 118,  258.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  6.20, 4, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 119, 125,  298.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  6.60, 3, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 126, 132,  339.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  7.10, 3, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 133, 139,  384.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  7.70, 3, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 140, 146,  430.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  8.60, 3, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 147, 153,  480.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  9.10, 3, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 154, 160,  531.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  9.60, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 161, 167,  585.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  9.90, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 168, 174,  640.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000,  9.60, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 175, 181,  696.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000, 10.00, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 182, 188,  754.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000, 10.60, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 189, 195,  815.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000, 10.80, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 196, 202,  878.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000, 11.00, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 203, 209,  940.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000, 11.30, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40'),
 (1, 210, 216, 1000.00, NULL, NULL, NULL,     28, 28, 8,    8,    1000, 12.00, 2, 'Manual Pacú y Tambaquí 5ta ed., p.40');


-- ============================================================
-- WaterQualityReference  (manual p.20-24, 27)
-- ============================================================
INSERT INTO WaterQualityReference
 (SpeciesId, Parameter, Unit, OptimalMin, OptimalMax, WarningMin, WarningMax, CriticalMin, CriticalMax, RecommendedAction, Source)
VALUES
 (1, 'DISSOLVED_OXYGEN', 'mg/L', 4, NULL, 2, NULL, 1, NULL,
  'Stop feeding and fertilizing. Exchange at least 25% of the water (drop it from 1 m height). Pump surface water, reduce fish numbers without entering the pond, apply probiotics, install aerators.',
  'Manual Pacú y Tambaquí 5ta ed., p.20-22, 27'),
 (1, 'PH', 'pH', 7, 8.5, 5, 10, 4, 11,
  'If low, apply agricultural lime according to the dose table (p.17). Measure at about 06:00 and 18:00; the difference between both readings must not exceed 2 units.',
  'Manual Pacú y Tambaquí 5ta ed., p.24, 27'),
 (1, 'TEMPERATURE', 'C', 25, 32, 20, 37, 16, 38,
  'Reduce or suspend feeding until the temperature stabilizes. Avoid handling the fish.',
  'Manual Pacú y Tambaquí 5ta ed., p.24'),
 (1, 'TRANSPARENCY', 'cm', 25, 40, NULL, NULL, 25, NULL,
  'Below 25 cm: do not fertilize, do not feed, exchange about 25% of the water. Above 40 cm: fertilize.',
  'Manual Pacú y Tambaquí 5ta ed., p.23, 27');

