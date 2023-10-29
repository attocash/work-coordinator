package cash.atto.work

data class WorkRequested(val callbackUrl: String, val hash: String, val threshold: ULong)
data class WorkGenerated(val callbackUrl: String, val hash: String, val threshold: ULong, val work: String)