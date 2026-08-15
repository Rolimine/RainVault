# RainVault

RainVault — плагин экономики для серверов **Paper/Spigot 1.16.5+**. Он хранит балансы игроков в MySQL, предоставляет провайдер `Economy` через Vault и поэтому совместим с плагинами магазинов, квестов, мини-игр и другими расширениями, использующими Vault API.

## Возможности

- Экономика игроков с начальным и максимальным балансом.
- Интеграция с **Vault**: RainVault регистрируется как `Economy`-провайдер с высоким приоритетом.
- Переводы между игроками: `/pay <игрок> <сумма>`.
- Возможность игроку отключить входящие переводы: `/pay toggle`.
- Администрирование баланса онлайн- и офлайн-игроков.
- Топ богатейших игроков: `/baltop`.
- PlaceholderAPI-плейсхолдеры баланса и топа.
- Асинхронная работа с MySQL через HikariCP и локальный кэш балансов.
- Настраиваемые сообщения, отображение топа и формат чисел.

## Требования

| Компонент | Требование |
| --- | --- |
| Сервер | Paper / Spigot API 1.16.5 или новее |
| Java | 17 |
| Обязательно | Vault, MySQL 8+ |
| Необязательно | PlaceholderAPI |

`Vault` — обязательная зависимость: без него RainVault выключится при запуске. MySQL также фактически обязателен: текущая версия всегда создаёт подключение к БД, независимо от значения `mysql.enabled`.

## Установка

1. Установите Vault и любой совместимый сервер с Java 17.
2. Соберите плагин или возьмите готовый JAR из `target/RainVault-1.0-SNAPSHOT.jar`.
3. Поместите JAR RainVault и Vault в папку `plugins/` сервера.
4. Запустите сервер один раз — будут созданы `config.yml`, `baltop.yml` и `rvaulttop.yml` в `plugins/RainVault/`.
5. Остановите сервер, укажите реквизиты MySQL в `plugins/RainVault/config.yml`, затем запустите его снова.
6. При необходимости установите PlaceholderAPI. RainVault зарегистрирует своё расширение автоматически.

При первом обращении к балансу создаётся запись игрока с `starting-balance`. Таблица MySQL создаётся автоматически и содержит поля `uuid`, `balance`, `pay_toggle`.

## Конфигурация

Основные параметры находятся в `plugins/RainVault/config.yml`:

```yml
starting-balance: 1000
max-balance: 100000000000
debug: false

mysql:
  enabled: true # зарезервировано; в текущей версии MySQL используется всегда
  host: localhost
  port: 3306
  database: minecraft
  username: rainvault
  password: "change-me"
  table: RainVault
```

В секции `messages` можно изменить префикс и все сообщения. Поддерживаются цветовые коды через `&` и подстановки `%player%`, `%amount%` там, где они используются сообщением.

### Топ балансов

- `rvaulttop.yml` — формат `%rainvault_top_N%`, размер топа `top-size` и период фонового обновления `update-interval` в секундах.
- `baltop.yml` — заголовок, строки, подвал, тексты загрузки и пустого топа для `/baltop`.

В строке топа доступны `%position%`, `%player%`, `%balance%`, `%formatted_balance%`.

## Команды

| Команда | Назначение | Право |
| --- | --- | --- |
| `/pay <игрок> <сумма>` | Перевести деньги онлайн-игроку | `rainvault.pay` |
| `/pay toggle` | Включить или выключить получение переводов | `rainvault.pay` |
| `/baltop` | Показать топ балансов | `rainvault.baltop` |
| `/rainvault give <игрок> <сумма>` | Выдать средства | `rainvault.admin` |
| `/rainvault set <игрок> <сумма>` | Установить баланс | `rainvault.admin` |
| `/rainvault take <игрок> <сумма>` | Снять средства | `rainvault.admin` |
| `/rainvault check <игрок>` | Проверить баланс | `rainvault.admin` |
| `/rainvault reset <игрок>` | Сбросить баланс к стартовому | `rainvault.admin` |
| `/rainvault reload` | Перезагрузить конфиги и задачу топа | `rainvault.reload` или `rainvault.admin` |

Алиасы: `/rv` для `/rainvault`, `/balancetop` для `/baltop`.

## Права

| Право | По умолчанию | Назначение |
| --- | --- | --- |
| `rainvault.admin` | OP | Административные подкоманды |
| `rainvault.pay` | Все | Переводы и переключатель приёма |
| `rainvault.reload` | OP | Перезагрузка |
| `rainvault.baltop` | Все | Просмотр топа |
| `rainvault.bypass.max` | Нет | Зарезервировано для обхода лимита |
| `rainvault.bypass.toggle` | Нет | Зарезервировано для обхода запрета переводов |

Последние два права объявлены в `plugin.yml`, но в текущей реализации обработчики команд их не применяют.

## PlaceholderAPI

После установки PlaceholderAPI доступны:

| Плейсхолдер | Результат |
| --- | --- |
| `%rainvault_balance%` | Форматированный баланс игрока |
| `%rainvault_top_1%` | Первая строка топа |
| `%rainvault_top_N%` | Строка с позицией `N`, в формате из `rvaulttop.yml` |

Например, `%rainvault_top_3%` может вывести `3. Player — 25,000 coins`. Если позиции нет, возвращается `N/A`.

## Интеграция для разработчиков

### Рекомендуемый путь: Vault API

Это стабильный способ двусторонней совместимости:

- RainVault **предоставляет** реализацию интерфейса Vault `Economy`.
- Ваш плагин **запрашивает** этот сервис и не зависит от внутренних классов RainVault.
- Когда другой плагин вызывает `depositPlayer` или `withdrawPlayer`, изменения попадают в баланс RainVault. Когда ваш плагин вызывает те же методы, он работает с активной Vault-экономикой, которой является RainVault.

Добавьте Vault как `provided`-зависимость и укажите мягкую зависимость, чтобы ваш плагин загрузился после RainVault:

```yml
# plugin.yml вашего плагина
softdepend: [Vault, RainVault]
```

```xml
<!-- pom.xml вашего плагина -->
<dependency>
    <groupId>com.github.MilkBowl</groupId>
    <artifactId>VaultAPI</artifactId>
    <version>1.7</version>
    <scope>provided</scope>
</dependency>
```

Получите сервис при включении своего плагина:

```java
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

private Economy economy;

private boolean hookEconomy() {
    RegisteredServiceProvider<Economy> registration = getServer()
            .getServicesManager()
            .getRegistration(Economy.class);

    if (registration == null) {
        getLogger().warning("Vault Economy provider не найден");
        return false;
    }

    economy = registration.getProvider();
    return economy != null;
}
```

Использование — всегда проверяйте `EconomyResponse`:

```java
EconomyResponse result = economy.withdrawPlayer(player, price);
if (!result.transactionSuccess()) {
    player.sendMessage("Недостаточно средств: " + result.errorMessage);
    return;
}

economy.depositPlayer(player, reward);
String text = economy.format(economy.getBalance(player));
```

Доступны обычные методы Vault: `getBalance`, `has`, `withdrawPlayer`, `depositPlayer`, `format` и их варианты с `OfflinePlayer`. Банки RainVault не поддерживает: все методы банков возвращают `NOT_IMPLEMENTED`.

### Прямой доступ к RainVault

Если требуются возможности, которых нет в Vault, можно получить экземпляр плагина и использовать публичные менеджеры. Этот путь связывает ваш код с внутренней структурой RainVault, поэтому для универсальных плагинов предпочтителен Vault.

```java
import org.bukkit.plugin.java.JavaPlugin;
import rd.rolidev.rainvault.RainVault;

RainVault rainVault = JavaPlugin.getPlugin(RainVault.class);

rainVault.getTransactionManager()
        .deposit(player, 500.0)
        .thenAccept(success -> {
            if (success) {
                // Перейдите в основной поток Bukkit,
                // прежде чем менять мир или отправлять сообщения.
            }
        });
```

Публичные точки расширения текущей версии:

| Компонент | Возможности |
| --- | --- |
| `getDatabaseManager()` | Асинхронные `getBalance(UUID)`, `getPayToggle(UUID)`, `getTopBalances(int)`; `setBalance(UUID, double)` и `setPayToggle(UUID, boolean)` сохраняют изменения асинхронно |
| `getTransactionManager()` | `executeTransaction`, `deposit`, `withdraw`, `setBalance`, `resetBalance`; результаты методов — `CompletableFuture` |
| `getEconomyManager()` | Проверка сумм, форматирование и разбор суффиксов `k`, `m`, `mm` |
| `getTopManager()` | Кэш топа, `getTopEntry`, `getPlayerPosition`, `getCachedTop` |
| `getStatisticsManager()` | Статистика в памяти текущего запуска |
| `getConfigManager()` | Доступ к загруженным конфигурациям |

Для прямой интеграции добавьте RainVault как зависимость только на этапе компиляции и объявите в своём `plugin.yml`:

```yml
depend: [RainVault]
```

Не обращайтесь к `DatabaseManager` напрямую из Bukkit main thread через `.get()` или `.join()` — чтение БД асинхронно. Обработайте `CompletableFuture`, а для Bukkit API вернитесь в основной поток через `Bukkit.getScheduler().runTask(...)`.

### Передача между двумя игроками

Для перевода с проверками используйте `TransactionManager` и модель `Transaction`:

```java
import rd.rolidev.rainvault.models.Transaction;

Transaction tx = new Transaction(
        sender,
        receiver,
        250.0,
        Transaction.TransactionType.PLAYER_TO_PLAYER
);

rainVault.getTransactionManager().executeTransaction(tx)
        .thenAccept(result -> {
            if (!result.isSuccess()) {
                getLogger().warning("Перевод отклонён: " + result.getMessage());
            }
        });
```

Менеджер проверяет существование игроков, положительность суммы, доступность средств и максимальный баланс получателя. Транзакции и статистика не сохраняются в отдельную историю: `StatisticsManager` живёт только до перезапуска, а `LoggerUtil` и `StatsCommand` в этой версии не подключены к зарегистрированным командам/потоку переводов.

## Сборка из исходников

```powershell
mvn clean package
```

Готовый shaded JAR появится в `target/RainVault-1.0-SNAPSHOT.jar`. Зависимости Paper, Vault и PlaceholderAPI не включаются в него; HikariCP и MySQL-драйвер включаются.

## Важные замечания по эксплуатации

- Не публикуйте реальные данные MySQL. Если в уже отслеживаемом `config.yml` были учётные данные, смените пароль в БД и удалите секреты из истории Git перед публикацией репозитория.
- Название `mysql.table` подставляется в SQL как имя таблицы. Используйте только контролируемое простое имя без пробелов и спецсимволов.
- Балансы хранятся как `DOUBLE`, а вывод форматируется без дробной части. Для цен с дробями заранее проверьте, устраивает ли это ваш сервер.
- `/pay` работает только с онлайн-получателем. Административные команды принимают `OfflinePlayer`.

## Лицензия и поддержка

Лицензия в репозитории пока не указана. Перед распространением добавьте файл `LICENSE` и канал поддержки/issue tracker.
