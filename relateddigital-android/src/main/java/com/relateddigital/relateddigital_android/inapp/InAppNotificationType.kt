package com.relateddigital.relateddigital_android.inapp

enum class InAppNotificationType {
    UNKNOWN {
        override fun toString(): String {
            return "unknown"
        }
    },
    MINI {
        override fun toString(): String {
            return "mini"
        }
    },
    HALF_SCREEN {
        override fun toString(): String {
            return "half_screen_image"
        }
    },
    FULL {
        override fun toString(): String {
            return "full"
        }
    },
    SMILE_RATING {
        override fun toString(): String {
            return "smile_rating"
        }
    },
    IMAGE_TEXT_BUTTON {
        override fun toString(): String {
            return "image_text_button"
        }
    },
    FULL_IMAGE {
        override fun toString(): String {
            return "full_image"
        }
    },
    IMAGE_BUTTON {
        override fun toString(): String {
            return "image_button"
        }
    },
    NPS {
        override fun toString(): String {
            return "nps"
        }
    },
    ALERT {
        override fun toString(): String {
            return "alert"
        }
    },
    NPS_WITH_NUMBERS {
        override fun toString(): String {
            return "nps_with_numbers"
        }
    },
    CAROUSEL {
        override fun toString(): String {
            return "carousel" //TODO: Check this string when the real data comes.
        }
    },
    NPS_AND_SECOND_POP_UP {
        override fun toString(): String {
            return "nps_with_secondpopup"
        }
    }
}