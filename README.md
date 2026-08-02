# Arcana

Arcana — неофициальная портировка Thaumcraft 6 для Minecraft 1.20.1 с загрузчиком Forge (modid=`arcana`). Проект восстанавливает и адаптирует механики магии, исследования и мира из TC6, добавляя собственные механики, предметы и генерацию мира.

Версия: **1.1.0** — Phase N  
Документы: `PORT_PLAN.md`, `arcana_changelog.txt`, `SMOKE_CHECKLIST.md`, `NOTICE.md`, `CREDITS.md`.

---

## Кратко

- Порт Thaumcraft 6 (неофициальный) на Minecraft 1.20.1 + Forge.
- Фокус: аспекты, инфузия, исследования, големы, мирогенерация и Eldritch-тематика.
- Поддержка soft-dependencies: JEI, Curios.
- Язык: Java (требуется Java 17).

---

## Требования

- Java 17 (например, Eclipse Adoptium / OpenJDK 17)
- Minecraft 1.20.1 + Forge 47.x
- Рекомендуемые (soft): JEI, Curios
- Построение: Gradle (включены `gradlew` / `gradlew.bat`)

---

## Быстрый старт — сборка и запуск

Windows PowerShell:
```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.xx"
.\gradlew.bat compileJava
.\gradlew.bat jar
.\gradlew.bat runClient
```

Unix / macOS:
```bash
export JAVA_HOME="/path/to/jdk-17"
./gradlew compileJava
./gradlew jar
./gradlew runClient
```

Артефакт: `build/libs/arcana-1.1.0.jar`

---

## Настройка Curios (soft-dependency)

- В пользовательской среде разработки (FG userdev) по умолчанию Curios отключён, чтобы избежать проблем с mixin-ом при официальных mappings.
- Для реального клиента (сборки для релиза) включите Curios runtime: в `gradle.properties` установите `arcana.enable_curios_runtime=true` или запускайте Gradle с `-Parcana.enable_curios_runtime=true`.
- В `gradle.properties` есть пояснения и рекомендации по взаимодействию с Curios.

---

## Конфигурация (COMMON)

После первого запуска в `config/arcana-common.toml` будут доступные ключи, например:
- `auraNodeRegenMultiplier` (default 1.0)
- `warpEventChanceMultiplier` (default 1.0)
- `worldgenStructureRarityMultiplier` (default 1.0)
- `stickyWarpDecayTicks`, `tempWarpDecayTicks`
- `infusionStabilityMultiplier`, `focusVisCostMultiplier`
- `golemWorkIntervalTicks`, `theorycraftInspirationBase`

Изменяйте значения для балансировки или тестирования.

---

## Основные возможности

- Система аспектов, кристаллы, таумиум/void-броня и аксессуары (очки, ботинки).
- Исследования и книга знаний (BASICS, AUROMANCY, ARTIFICE, ALCHEMY, INFUSION, GOLEMANCY, ELDRITCH — ~148 записей).
- Инфузия (столбы, стабильность, фокусы), фокусный манипулятор.
- Крафтовые и алхимические машины: crucible, arcane workbench, smelter, alembic, centrifuge.
- Устройства: levitator, magic mirror, lamp of growth, hungry chest.
- Големы с заданиями: gather, guard, fill, empty, harvest, use, butcher.
- Генерация мира: greatwood/silverwood, crystal clusters, flux patches, eldritch-структуры и Outer Lands (как биом/пачки).
- Враги и угрозы: Crimson Cultists, Eldritch Guardians, mind spiders.
- Поддержка локалей: `en_us`, `ru_ru`. Soft JEI-интеграция и дерево достижений.

---

## Команды (игровые)

- `/arcana` или `/arcana help` — справка
- `/arcana aspects`
- `/arcana smoke` — тестовые проверки регистраций
- Администраторские команды: `research`, `knowledge`, `warp` (PERMANENT|STICKY|TEMPORARY), `aura`, `essentia`, `crucible`, `cast`, `focus give`

---

## Outer Lands и мирогенерация

- Arcana включает datapack-биом `arcana:outer_lands` (тёмно-фиолетовый туман/небо/вода), но он не внедряется в overworld без TerraBlender.
- Реализовано редкое появление «карманов» Outer Lands (feature) — однотипные области с порталами/ноды/культистами и т. п.
- TerraBlender потребуется для маппинга биома в мир (будет добавлен при необходимости).

---

## Публикация модa

Нет автоматической CI для Modrinth/CurseForge; ручная публикация:
1. Собрать jar: `./gradlew jar` (или `.\gradlew.bat jar --offline`).
2. Загрузить `build/libs/arcana-1.1.0.jar` на Modrinth / CurseForge.
3. Указать game version `1.20.1`, loader `Forge`, и опциональные зависимости (JEI / Curios).
4. В описание вставить релиз-описание из `arcana_changelog.txt` и ссылку на `NOTICE.md` / Credits.

---

## Roadmap

Состояние разработки и планы находятся в `PORT_PLAN.md` (фазы N и выше). См. `SMOKE_CHECKLIST.md` для контрольных пунктов перед релизами.

---

## Лицензия, авторство и юридические замечания

- Arcana — неофициальный проект, вдохновлён Thaumcraft 6 (Azanor). Проект не связан с Mojang, Microsoft или Azanor.
- Оригинальные материалы TC6 могут использоваться как справочные; текущий код Arcana — All Rights Reserved (см. `gradle.properties` / `mods.toml`).
- Forge MDK и сопутствующие библиотеки следуют своим лицензиям (см. `LICENSE.txt`).
- Подробности: `NOTICE.md`, `CREDITS.md`.

---

## Вклад и отчёт об ошибках

- Хотите внести изменения или сообщить об ошибке — откройте Issue или Pull Request в этом репозитории.
- Перед PR убедитесь, что изменения документированы в changelog и проходят локальный запуск/сборку.

---

## Контакты

Автор: **Nanda070**
Ссылки в репозитории: `NOTICE.md`, `CREDITS.md`, `arcana_changelog.txt`, `PORT_PLAN.md`.

---

Спасибо за интерес к Arcana — если нужно, я могу:
- Записать этот README.md в репозиторий (commit).
- Подготовить сокращённую англоязычную версию.
- Сформировать шаблон релиза для Modrinth/CurseForge.
