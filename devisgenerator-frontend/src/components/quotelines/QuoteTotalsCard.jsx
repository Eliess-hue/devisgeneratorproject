export default function QuoteTotalsCard({
                                            totalHt,
                                            totalTva,
                                            totalTtc
                                        }) {

    return (

        <div className="w-full flex justify-end">

            <div
                className="
                    card
                    bg-base-200
                    border
                    border-base-300
                    rounded-lg
                    w-full
                    max-w-sm
                "
            >

                <div className="card-body">

                    <h3 className="font-semibold">

                        Totaux

                    </h3>

                    <div className="space-y-2">

                        <div className="flex justify-between">

                            <span>
                                Total HT
                            </span>

                            <span>
                                {Number(totalHt ?? 0).toFixed(2)} €
                            </span>

                        </div>

                        <div className="flex justify-between">

                            <span>
                                TVA (20%)
                            </span>

                            <span>
                                {Number(totalTva ?? 0).toFixed(2)} €
                            </span>

                        </div>

                    </div>

                    <div className="divider my-2"></div>

                    <div className="flex justify-between text-xl font-bold">

                        <span>
                            Total TTC
                        </span>

                        <span className="text-blue-500">

                            {Number(totalTtc ?? 0).toFixed(2)} €

                        </span>

                    </div>

                </div>

            </div>

        </div>

    )

}