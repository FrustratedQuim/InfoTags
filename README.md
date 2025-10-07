# Как это понимать? (RU)

## Описание

Мод `InfoTags` - пример реализации обмена данными между клиентом и сервером. Используется передача пакетов через кастомный канал.

## Зависимости

- Fabric API - https://modrinth.com/mod/fabric-api
- Minecraft 1.21.2 - 1.21.8
- DataTransfer на стороне сервера - https://github.com/FrustratedQuim/DataTransfer

## Как работает мод

1. **Инициализация**: Мод регистрирует канал `datatransfer:main` для двустороннего обмена данными.
2. **Handshake**: При входе в мир мод отправляет пакет-хэндшейк для получения доступа и возможности запроса данных.
3. **Запрос данных**: При соблюдении условий, мод отправляет пакет с определённым контентом для запроса данных. Остальное уже на стороне сервера. 
4. **Отображение данных**: Используется предоставляемый игрой TextDisplay, так-как это самый простой и внешне привычный вариант.

---

# How to understand this? (EN)

## Description

The `InfoTags` mod is an example of implementing data exchange between the client and the server. It uses packet transmission through a custom channel.

## Dependencies

- Fabric API - https://modrinth.com/mod/fabric-api
- Minecraft 1.21.2 - 1.21.8
- DataTransfer on the server side - https://github.com/FrustratedQuim/DataTransfer

## How the mod works

1. **Initialization**: The mod registers the channel `datatransfer:main` for two-way data exchange.
2. **Handshake**: When entering the world, the mod sends a handshake packet to gain access and the ability to request data.
3. **Data Request**: When certain conditions are met, the mod sends a packet with specific content to request data. The rest is handled on the server side.
4. **Data Display**: The mod uses the built-in `TextDisplay`, as it is the simplest and most visually familiar option.
