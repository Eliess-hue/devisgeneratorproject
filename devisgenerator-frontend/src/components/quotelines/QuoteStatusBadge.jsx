export default function QuoteStatusBadge({
                                             status
                                         }) {

    const styles = {
        DRAFT:
            'badge px-3 py-3 bg-slate-700 text-slate-200 border-none rounded-lg',

        PENDING:
            'badge px-3 py-3 bg-[#0B2E5B] text-[#4EA1FF] border-none rounded-lg',

        ACCEPTED:
            'badge px-3 py-3 bg-[#064E3B] text-[#34D399] border-none rounded-lg',

        REFUSED:
            'badge px-3 py-3 bg-[#7F1D1D] text-[#FCA5A5] border-none rounded-lg',

        EXPIRED:
            'badge px-3 py-3 bg-[#78350F] text-[#FCD34D] border-none rounded-lg'
    }

    const labels = {
        DRAFT: 'Brouillon',
        PENDING: 'En attente',
        ACCEPTED: 'Accepté',
        REFUSED: 'Refusé',
        EXPIRED: 'Expiré'
    }

    return (
        <span
            className={
                styles[status] ||
                'badge bg-slate-700 text-slate-200 border-none rounded-lg'
            }
        >
            {labels[status] || status}
        </span>
    )
}