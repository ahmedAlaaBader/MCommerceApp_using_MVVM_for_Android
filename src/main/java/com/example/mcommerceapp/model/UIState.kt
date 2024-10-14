package com.example.mcommerceapp.model






// Sealed class to represent the various states of the UI
sealed class UIState {
    // Represents a successful state with the retrieved data
    class Success<T> (val data :T): UIState()

    // Represents a failure state with an error message
    class Failure(val msg: Throwable) : UIState()

    // Represents a loading state while data is being fetched
    object Loading : UIState()

    // Represents a state when no data is available
    object NoData : UIState()
}
