package com.example.projectappmobile

interface GlobalContracts {
    interface View {
        fun onResume()
        fun onPause()
        fun showProgressBar()
        fun hideProgressBar()
    }

    interface Presenter {
        fun onResume()
        fun onPause()
        fun onShowGeneralShortMessage(msg: String)
        fun onShowGeneralLongMessage(msg: String)
    }

    interface Interactor {
        fun onResume()
        fun onPause()
    }

}