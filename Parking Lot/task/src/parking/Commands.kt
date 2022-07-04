package parking

enum class Commands(val commandName: String) {
    PARK("park"),
    LEAVE("leave"),
    EXIT("exit"),
    STATUS("status"),
    CREATE("create"),
    SPOT_BY_COLOR("spot_by_color"),
    REG_BY_COLOR("reg_by_color"),
    SPOT_BY_REG("spot_by_reg")
}