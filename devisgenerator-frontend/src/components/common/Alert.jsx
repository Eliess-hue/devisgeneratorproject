import { useEffect } from "react"

export default function Alert({
                                  type = "error",
                                  children,
                                  className = "",
                                  autoClose = null,
                                  onClose = null
                              }) {

    useEffect(() => {

        if (!autoClose || !onClose) {
            return
        }

        const timer = setTimeout(() => {

            onClose()

        }, autoClose)

        return () => {

            clearTimeout(timer)

        }

    }, [autoClose, onClose])

    const variants = {

        error: `
            bg-[var(--color-error-bg)]
            border-[var(--color-error-border)]
            text-[var(--color-error-text)]
        `,

        success: `
            bg-[var(--color-success-bg)]
            border-[var(--color-success-border)]
            text-[var(--color-success-text)]
        `

    }

    return (

        <div
            className={`
                rounded-lg
                border
                px-4
                py-3
                ${variants[type]}
                ${className}
            `}
        >

            {children}

        </div>

    )

}