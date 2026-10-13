esta es la base de datos del proyecto, leela y entiendela no necesito que la implementes por que por ahora solo estamos trabajando en frontend, quiero que la tomes muy en cuenta para las interfaces y todos los datos necesarios para la interfaz de usuario, asi que leela y entiendela y haz cambios que consideres necesarios y coherentes para la interfaz de usuario, recuerda siempre mantener el estilo y coherencia de las pantallas
-- ============================================================
-- FISH FARM DATABASE
-- ============================================================

DROP DATABASE IF EXISTS PisciDataDB;

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
        ON UPDATE CASCADE,

    CONSTRAINT CK_User_Role
        CHECK (Role IN ('Administrator', 'Technician', 'Worker','Owner'))
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