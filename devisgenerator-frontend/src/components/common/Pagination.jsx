export default function Pagination({
                                       page,
                                       totalPages,
                                       totalElements,
                                       onPageChange
                                   }) {

    if (totalPages <= 1) {
        return null;
    }

    return (

        <div className="flex items-center justify-between mt-6">

    <span className="text-sm text-base-content/60">
        {totalElements} devis
    </span>

            <div className="flex items-center gap-4">

                <button
                    className="btn btn-outline btn-sm rounded-lg"
                    disabled={page === 0}
                    onClick={() => onPageChange(page - 1)}
                >
                    ← Précédent
                </button>

                <span>
            Page {page + 1} sur {totalPages}
        </span>

                <button
                    className="btn btn-outline btn-sm rounded-lg"
                    disabled={page === totalPages - 1}
                    onClick={() => onPageChange(page + 1)}
                >
                    Suivant →
                </button>

            </div>

        </div>

    );

}