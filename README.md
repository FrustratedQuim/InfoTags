# Как это понимать? (RU)

## Описание

Мод `InfoTags` - пример реализации обмена кастомными данными между модом на клиенте и плагином на сервере. Всё реализовано через собственные каналы, по которым просто предаются пакеты.

## Зависимости

- Fabric API - https://modrinth.com/mod/fabric-api
- Minecraft 1.21.X
- DataTransfer на стороне сервера - https://github.com/FrustratedQuim/DataTransfer

## Как работает мод

1. **Инициализация**: Мод регистрирует каналы `datatransfer:handshake` и `datatransfer:playerinfo_request` (клиент -> сервер) и `datatransfer:playerinfo` (сервер -> клиент).
2. **Handshake**: При входе в мир мод отправляет пакет `datatransfer:handshake` с особыми данными для связи с сервером.
3. **Запрос данных**: Когда игрок смотрит на другого игрока, мод запрашивает данные через `datatransfer:playerinfo_request`. Сервер отвечает данными через `datatransfer:playerinfo`. Конкретно в этой реализации всё сделано через взгляд, но по итогу можно запрашивать данные любым иным способом.
4. **Отображение данных**: Данные (имя, здоровье, голод) отображаются над головой игрока с помощью `TextDisplay`. Стандартный никнейм и броня скрываются для лучшей видимости. Можно конечно реализовать через кастомный рендер текста, но `TextDisplay` уже предоставляется майнкрафтом - почему бы и не юзать. p.s. На некоторых серверах используется TextDisplay в качестве кастомных ников, поэтому отключение рендера таковых тоже реализовано.

---

# How to understand this? (EN)

## Description

The `InfoTags` mod is an example of implementing custom data exchange between a client-side mod and a server-side plugin. Everything is implemented through custom channels that simply transmit packets.

## Dependencies

- Fabric API - https://modrinth.com/mod/fabric-api
- Minecraft 1.21.X
- DataTransfer on the server side - https://github.com/FrustratedQuim/DataTransfer

## How the mod works

1. **Initialization**: The mod registers the channels `datatransfer:handshake` and `datatransfer:playerinfo_request` (client -> server) and `datatransfer:playerinfo` (server -> client).
2. **Handshake**: When joining a world, the mod sends a `datatransfer:handshake` packet with specific data to establish a connection with the server.
3. **Data Request**: When the player looks at another player, the mod requests data via `datatransfer:playerinfo_request`. The server responds with data through `datatransfer:playerinfo`. In this specific implementation, it’s done via line-of-sight, but ultimately, data can be requested in any other way.
4. **Data Display**: The data (name, health, hunger) is displayed above the player’s head using `TextDisplay`. The default nickname and armor are hidden for better visibility. While it’s possible to implement this through custom text rendering, `TextDisplay` is provided by Minecraft—so why not use it? P.S. Some servers use `TextDisplay` for custom nicknames, so disabling the rendering of those is also implemented.