package ru.eaosipov.imdrived.app.src.kotlin.client.error

import java.io.IOException

/**
 * Функция для проверки наличия интернет-соединения.
 * Использует ICMP-запрос (ping) к серверу Google (8.8.8.8).
 *
 * @return `true`, если устройство имеет доступ в интернет, `false` в противном случае.
 */
fun isOnline(): Boolean {
    // Получаем объект Runtime для выполнения системных команд
    val runtime = Runtime.getRuntime()
    try {
        // Выполняем команду ping к серверу Google (8.8.8.8)
        // -c 1 означает отправку только одного пакета
        val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")

        // Ожидаем завершения процесса и получаем код возврата
        val exitValue = ipProcess.waitFor()

        // Код возврата 0 означает успешное выполнение команды (интернет есть)
        return (exitValue == 0)
    } catch (e: IOException) {
        // Ловим исключение, если возникла ошибка ввода-вывода (например, команда не найдена)
        e.printStackTrace()
    } catch (e: InterruptedException) {
        // Ловим исключение, если поток был прерван во время ожидания завершения процесса
        e.printStackTrace()
    }

    // Если произошла ошибка, считаем, что интернета нет
    return false
}