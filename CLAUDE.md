# MoFa Android — Project Guide for Claude Code

## Project Overview

**MoFa** (Mobile Farming) is an Android agricultural work-journal app. Farmers use it to log field work: spraying, fertilization, harvests, irrigation, and general labour. Data syncs to a backend (ASA Agrar or Excel-based) via HTTP/XML/JSON, or via Dropbox.

- **App ID:** `it.bz.tol.mofa`
- **Namespace:** `it.schmid.android.mofa`
- **Version:** 7.0 (versionCode 42)
- **Min SDK:** 26 (Android 8.0) · **Target/Compile SDK:** 36
- **Java:** 17

---

## Build System

```bash
# Assemble debug APK
./gradlew :mofa:assembleDebug

# Assemble release APK (ProGuard enabled)
./gradlew :mofa:assembleRelease

# Install debug build on connected device
./gradlew :mofa:installDebug

# Run lint
./gradlew :mofa:lint
```

### Modules

| Module | Purpose |
|--------|---------|
| `mofa` | Main application (`it.bz.tol.mofa`) |
| `library` | Repackaged Google Play Licensing library (`com.google.android.vending.licensing`) — no external deps |

### Version Catalog

All dependency versions are centralised in `gradle/libs.versions.toml`. Add new dependencies there, never hardcode versions in `build.gradle`.

### Key Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| `androidx.appcompat` | 1.7.1 | AppCompat base |
| `com.google.android.material` | 1.13.0 | Material Design 3 components |
| `com.google.code.gson` | 2.13.2 | JSON serialisation |
| `com.google.http-client:google-http-client-android` | 2.1.0 | HTTP client for backend API |
| `com.dropbox.core:dropbox-android-sdk` | 7.0.0 | Dropbox sync (includes core) — do NOT add `dropbox-core-sdk` separately (duplicate classes) |
| `com.squareup.okhttp3:okhttp` | 5.3.2 | HTTP client (OkHttpClient in MofaApplication) |
| `org.jsoup:jsoup` | 1.22.1 | HTML/XML parsing |
| `com.j256.ormlite:ormlite-android` | 6.1 | ORM database — do NOT add `ormlite-core` separately (duplicate classes, it is bundled inside `ormlite-android`) |

---

## Architecture

**Pattern:** Activity-Fragment MVC with singleton service layer.

```
HomeActivity (launcher)
  └── WorkOverviewActivity
        └── WorkEditTabActivity (tabbed editor)
              ├── WorkEditWorkFragment
              ├── WorkEditSprayFragment
              ├── WorkEditSoilFertilizerFragment
              ├── WorkEditHarvestFragment
              ├── WorkEditResourcesFragment
              └── WorkEditWaterFragment
```

### Base Classes

- **`DashboardActivity`** — All activities extend this. Provides `goHome()`, toast/trace utilities, and tablet layout handling.
- **`MofaApplication`** — Application singleton. Access via `MofaApplication.getInstance()`. Holds:
  - Global `ConcurrentHashMap` state
  - `OkHttpClient` instance
  - Work type constants: `WORK_NORMAL=1`, `WORK_SPRAY=2`, `WORK_FERT=3`
  - Backend software setting (1=ASA Agrar, other=Excel)
  - `AppStateListener` callbacks

### Database Layer

- **`DatabaseHelper`** — `OrmLiteSqliteOpenHelper`. Database name: `MofaDB.sqlite`, current version: **17**. All schema migrations live here (`updateFromVersion1` … `updateFromVersion17`). When adding a new column or table, increment `DATABASE_VERSION` and add a new `updateFromVersionN` method.
- **`DatabaseManager`** — Singleton DAO façade. Initialise once via `DatabaseManager.init(context)`, then access with `DatabaseManager.getInstance()`. All CRUD for every entity goes through here, never call DAOs directly from Activities.

#### Database Tables (19 total)

| Table | Key fields |
|-------|-----------|
| `Land` | id, name, code |
| `VQuarter` | id, name, code, size, data (variety quarter of a land) |
| `Worker` | id, name, code |
| `Machine` | id, name, code |
| `Task` | id, name, code, type, data |
| `Work` | id, date, task_id, note, valid, sended, data |
| `Pesticide` | id, productName, regNumber, defaultDose, code, constraints, barCode, data, status |
| `Fertilizer` | id, name, code, barCode, data |
| `SoilFertilizer` | id, name, code, data |
| `Spraying` | id, work_id, concentration, wateramount, weather |
| `SprayPesticide` | id (junction: Spraying ↔ Pesticide, with reason, periodCode) |
| `SprayFertilizer` | id (junction: Spraying ↔ Fertilizer) |
| `WorkVQuarter` | junction: Work ↔ VQuarter |
| `WorkWorker` | junction: Work ↔ Worker |
| `WorkMachine` | junction: Work ↔ Machine |
| `WorkFertilizer` | junction: Work ↔ Fertilizer |
| `Harvest` | id, work_id, pass, … |
| `FruitQuality` | sugar, pH, phenol, acid, stage |
| `Global` | global configuration key/value, workId |

### Sync / Import-Export

- **`WebServiceCall`** — `AsyncTask`-based HTTP import. Supports JSON and XML. Uses `ExecutorService` + `Handler` for background→UI thread handoff.
- **`SendingProcess`** — Exports unsent `Work` entries to the backend.
- **`ImportBehavior`** — Base class for models that parse JSON (`JSONArray`) or XML (`XmlPullParser`). Models annotated with Gson `@Expose` for JSON mapping.
- **`DropboxClient`** — OAuth2 PKCE. Credentials stored in `SharedPreferences`. App key: `kr7pjmpdjth06g0`.

---

## UI / Layouts

Layouts live in `mofa/src/main/res/layout/`. Key files:

| File | Purpose |
|------|---------|
| `activity_home.xml` | Dashboard — 3 `MaterialCardView` buttons (frame1/2/3) |
| `activity_home_button.xml` | Button template: `ImageButton` + `TextView` inflated into frame1/2/3 |
| `work_edit.xml` / `work_edit_main.xml` | Tabbed work editor container |
| `work_edit_spray.xml` | Spraying tab |
| `work_edit_soilfertilizer.xml` | Fertilizer tab |
| `work_edit_harvest.xml` | Harvest tab |
| `work_list.xml` | Work overview list |

**Theme:** `Theme.Material3.DayNight` (`@style/AppTheme` in `styles.xml`, applied globally in `AndroidManifest.xml`). Use Material3 components (`MaterialCardView`, `MaterialButton`, etc.) for all new UI work.

---

## Key Conventions

### Naming
- Activities: `*Activity`
- Fragments: `*Fragment`
- Dialogs: `*Dialog`
- Adapters: `*Adapter`
- DatabaseManager methods: `get*`, `create*`, `update*`, `delete*`, `flush*`

### Singleton Initialisation
```java
// Initialise once in onCreate (HomeActivity / Application)
DatabaseManager.init(context);
MofaApplication.getInstance(); // already available after Application.onCreate

// Use everywhere else
DatabaseManager.getInstance().getAllWorks();
```

### Adding a New Database Column
1. Add field to the model class with `@DatabaseField`
2. Increment `DATABASE_VERSION` in `DatabaseHelper`
3. Add `updateFromVersionN()` method with `ALTER TABLE` SQL
4. Add a `case N:` in `onUpgrade()` that calls `updateFromVersionN()` and then `onUpgrade(db, cs, oldVersion+1, newVersion)`

### Adding a New Table
1. Create model class with `@DatabaseTable` and `@DatabaseField` annotations
2. Add `Dao<Model, Integer>` field + getter in `DatabaseHelper`
3. Add `TableUtils.createTable(connectionSource, Model.class)` in `onCreate()`
4. Add `TableUtils.createTableIfNotExists(...)` in the appropriate upgrade method
5. Add CRUD methods in `DatabaseManager`

### Async / Threading
- Network operations use `ExecutorService` in `WebServiceCall` — do not call network on the main thread
- Use `Handler(Looper.getMainLooper())` to post results back to UI
- `TransactionManager.callInTransaction(connectionSource, callable)` for batch DB operations

### Backend Modes
- `backEndSoftware == "1"` → ASA Agrar mode (different import URLs, ASA-specific dialogs)
- Otherwise → Excel/generic mode
- Check with `MofaApplication.getInstance().getBackendSoftware()`

---

## Important Notes

- **ORMLite dual-JAR trap:** Only `ormlite-android:6.1` is needed — it already bundles `ormlite-core`. Adding `ormlite-core` separately causes hundreds of "Duplicate class" build errors.
- **Dropbox SDK dual-JAR trap:** Same issue — only `dropbox-android-sdk` is needed; `dropbox-core-sdk` is bundled inside it.
- **ProGuard:** `mofa/proguard-android.txt` contains custom rules. If new reflection-heavy libraries are added, ProGuard rules must be updated.
- **GPS:** `GPSLocationActivity` handles location for Land/VQuarter polygons — uses Android location APIs directly, no Google Maps dependency.
- **Tablet support:** `DashboardActivity` has special handling for large-screen layouts (see `large.xml`).
- **`Work.sended` flag:** Work entries are soft-deleted/archived by this flag. Never hard-delete without checking unsent works first.
