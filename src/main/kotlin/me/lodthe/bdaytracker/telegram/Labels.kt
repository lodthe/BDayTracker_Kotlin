package me.lodthe.bdaytracker.telegram

enum class MessageLabel(val label: String) {
    START("""
        Привет! Я умею напоминать про Дни Рождения (ДР) твоих друзей. 
        
        Ты можешь добавить какого-то друга вручную (`${ButtonLabel.ADD_FRIEND.label}`) или добавить даты Дней Рождения своих друзей из ВКонтакте (`${ButtonLabel.IMPORT_FROM_VK.label}`).
        
        Когда наступит чей-то День Рождения, я напомню тебе об этом, если вдруг ты сам забудешь (и никто об этом не узнает)!
        
        Чтобы импортировать друзей из VK, мне потребуется ID твоего профиля. Также, твой профиль должен быть открытым, когда ты захочешь импортировать друзей (потом можешь сделать его приватным).
        
        Обратим внимание, что у некоторых твоих друзей из VK скрыта дата рождения. Советую просмотреть весь список друзей в боте, в самом начала будут друзья, у которых скрыта дата рождения.
    """.trimIndent()),

    HELP(START.label),

    WRONG_COMMAND("""
        Кажется, ты отправил мне что-то не то.
    """.trimIndent()),

    MENU("""
        `${ButtonLabel.ADD_FRIEND.label}` — добавить вручную нового друга.
        
        `${ButtonLabel.IMPORT_FROM_VK.label}` — обновить даты Дней Рождения друзей из ВКонтакте
        
        `${ButtonLabel.UPDATE_VK_ID.label}` — обновить VK ID профиля, с которого будут импортироваться друзья
        
        `${ButtonLabel.LIST_OF_FRIENDS.label}` — просмотреть список уже добавленных друзей
    """.trimIndent()),

    UPDATE_VK_ID("""
        Введите новый VK ID. Он может содержать только цифры.
    """.trimIndent()),

    IMPORT_FROM_VK("""
        Список друзей из VK был успешно обновлен.
    """.trimIndent()),

    ID_HAS_CHANGED("""
        VK ID и список друзей из VK успешно обновлены.
        
        Новый VK ID: `%d`
    """.trimIndent()),

    ADD_FRIEND("""
        Чтобы добавить информацию о новом друге, отправь сообщение следующего вида:
        
        `Имя друга`
        `Дата рождения в формате ДД.ММ`
        
        Вторая строка должна содержать день и месяц рождения, сначала день, потом — месяц.
    """.trimIndent()),

    ADD_FRIEND_WRONG_LINES_COUNT("""
        Сообщение должно состоять из двух строк. 
        
        `Имя друга`
        `Дата рождения в формате ДД.ММ`
        
        Попробуй еще раз.
    """.trimIndent()),

    ADD_FRIEND_WRONG_DATE_FORMAT("""
        В дате сначала должен идти день, а за ним — месяц (они должны быть разделены точкой). День должен принадлежать диапазону \[1; 31], месяц — \[1; 12]. 
        
        Попробуй еще раз.
    """.trimIndent()),

    ADD_FRIEND_SUCCESS("""
        Новый друг успешно добавлен!
    """.trimIndent()),

    PROFILE_IS_CLOSED("""
        Кажется, профиль VK приватный, поэтому я не могу получить список друзей.
        
        Чтобы импортировать друзей VK с нового профиля, сделайте временно профиль VK открытым, обновите VK ID в боте, а затем можно опять сделать профиль приватным.
    """.trimIndent()),

    REMOVE_FRIEND("""
        Чтобы удалить друга, отправь мне его номер из списка друзей (идет перед фамилией), без точки.
        
        Если этот друг был добавлен из VK, вернуть его можно нажав `${ButtonLabel.IMPORT_FROM_VK.label}`
    """.trimIndent()),

    REMOVE_FRIEND_WRONG_FORMAT("""
        Чтобы удалить друга, отправь его номер из списка (просмотреть список можно нажав `${ButtonLabel.LIST_OF_FRIENDS.label}`). 
        
        Кажется, друга под таким номером нет. Попробовать еще раз можно нажав `${ButtonLabel.REMOVE_FRIEND.label}`.
    """.trimIndent()),

    REMOVE_FRIEND_SUCCESS("""
        %s успешно удален(а) из списка друзей.
    """.trimIndent()),

    FRIENDS_TO_CONGRATULATE_LIST("""
        Не забудь сегодня поздравить друзей с Днем Рождения!
        
        %s
    """.trimIndent()),

    LIST_OF_FRIENDS("")
}

enum class ButtonLabel(val label: String) {
    HELP("\uD83D\uDD39 Помощь"),
    ADD_FRIEND("\uD83D\uDC9A Добавить"),
    IMPORT_FROM_VK("\uD83D\uDD04 Обновить из VK"),
    UPDATE_VK_ID("✏️ Обновить VK ID"),
    LIST_OF_FRIENDS("\uD83D\uDC68\uD83C\uDFFC Список друзей"),
    LIST_OF_FRIENDS_PREVIOUS_PAGE("⬅️ Назад"),
    LIST_OF_FRIENDS_NEXT_PAGE("➡️ Вперед"),
    MENU("\uD83D\uDCDD Меню"),
    GET_ID("❓ Узнать VK ID"),
    REMOVE_FRIEND("❌ Удалить друга")
}

enum class TextLabel(val label: String) {
    GET_ID("https://vk.cc/75Fstp"),

    EMPTY_FRIEND_LIST("""
        К сожалению, пока что твой список друзей пуст. Ты можешь добавить друга вручную или импортировать друзей из VK.
    """.trimIndent()),

    NO_BIRTHDATE_DATA("""
        Нет информации
    """.trimIndent())
}