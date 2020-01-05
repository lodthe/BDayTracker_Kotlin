package me.lodthe.bdaytracker.telegram

enum class MessageLabel(val label: String) {
    START("""
        Привет! Я умею напоминать про Дни Рождения (ДР) твоих друзей. 
        
        Ты можешь добавить какого-то друга вручную (`${ButtonLabel.ADD_DATE.label}`) или добавить даты своих друзей из ВКонтакте (`${ButtonLabel.IMPORT_FROM_VK.label}`).
    """.trimIndent()),
    MENU("""
        `${ButtonLabel.ADD_DATE.label}` — добавить вручную новую дату ДР.
        
        `${ButtonLabel.IMPORT_FROM_VK.label}` — обновить даты ДР друзей из ВКонтакте
        
        `${ButtonLabel.UPDATE_VK_ID.label}` — обновить VK ID профиля, с которого будут импортироваться даты ДР друзей
        
        `${ButtonLabel.LIST_OF_USERS.label}` — просмотреть список уже добавленных ДР
    """.trimIndent()),
    UPDATE_VK_ID("""
        Введите новый VK ID. Он может содержать только цифры.
    """.trimIndent()),
    IMPORT_FROM_VK("""
        Список друзей из VK был успешно обновлен.
    """.trimIndent()),
    ID_HAS_CHANGED("""
        VK ID и список друзей из VK успешно обновлены.
    """.trimIndent())
}

enum class ButtonLabel(val label: String) {
    ADD_DATE("Добавить ДР"),
    IMPORT_FROM_VK("Обновить из VK"),
    UPDATE_VK_ID("Обновить VK ID"),
    LIST_OF_USERS("Список ДР"),
    MENU("Меню"),
    GET_ID("Узнать VK ID")
}

enum class TextLabel(val label: String) {
    GET_ID("https://vk.cc/75Fstp")
}