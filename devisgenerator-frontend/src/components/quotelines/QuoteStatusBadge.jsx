export default function QuoteStatusBadge({ status }) {

    const classes = {

        DRAFT:
            "badge px-3 py-3 rounded-lg border-none " +
            "bg-[var(--color-status-draft-bg)] " +
            "text-[var(--color-status-draft-text)]",

        PENDING:
            "badge px-3 py-3 rounded-lg border-none " +
            "bg-[var(--color-status-pending-bg)] " +
            "text-[var(--color-status-pending-text)]",

        ACCEPTED:
            "badge px-3 py-3 rounded-lg border-none " +
            "bg-[var(--color-status-accepted-bg)] " +
            "text-[var(--color-status-accepted-text)]",

        REFUSED:
            "badge px-3 py-3 rounded-lg border-none " +
            "bg-[var(--color-status-refused-bg)] " +
            "text-[var(--color-status-refused-text)]",

        EXPIRED:
            "badge px-3 py-3 rounded-lg border-none " +
            "bg-[var(--color-status-expired-bg)] " +
            "text-[var(--color-status-expired-text)]"

    }

    const labels = {

        DRAFT: "Brouillon",
        PENDING: "En attente",
        ACCEPTED: "Accepté",
        REFUSED: "Refusé",
        EXPIRED: "Expiré"

    }

    return (

        <span
            className={
                classes[status] ||
                "badge px-3 py-3 rounded-lg border-none"
            }
        >
            {labels[status] || status}
        </span>

    )

}