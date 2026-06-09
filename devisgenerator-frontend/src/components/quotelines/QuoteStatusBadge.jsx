export default function QuoteStatusBadge({
                                             status
                                         }) {

    const styles = {
        draft:
            'badge px-3 py-3 bg-slate-700 text-slate-200 border-none rounded-lg',

        pending:
            'badge px-3 py-3 bg-[#0B2E5B] text-[#4EA1FF] border-none rounded-lg',

        accepted:
            'badge px-3 py-3 bg-[#064E3B] text-[#34D399] border-none rounded-lg',

        refused:
            'badge px-3 py-3 bg-[#7F1D1D] text-[#FCA5A5] border-none rounded-lg'
    }

    const labels = {
        draft: 'Brouillon',
        pending: 'En attente',
        accepted: 'Accepté',
        refused: 'Refusé'
    }

    const normalizedStatus =
        status?.toLowerCase()

    return (
        <span
            className={
                styles[normalizedStatus] ||
                'badge bg-slate-700 text-slate-200 border-none rounded-lg'
            }
        >
            {labels[normalizedStatus] || status}
        </span>
    )
}