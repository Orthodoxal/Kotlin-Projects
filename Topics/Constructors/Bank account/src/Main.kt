// write the BankAccount class here
class BankAccount(val deposited: Long, val withdrawn: Long) {
    val balance = deposited - withdrawn
}