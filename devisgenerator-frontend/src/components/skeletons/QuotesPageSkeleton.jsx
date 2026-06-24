export default function QuotesPageSkeleton() {

    return (

        <div className="space-y-6">

            {/* Header */}
            <div className="flex justify-between items-center">

                <div className="skeleton h-10 w-48"></div>

                <div className="skeleton h-10 w-36"></div>

            </div>

            {/* Search */}
            <div className="skeleton h-12 w-full"></div>

            {/* Table */}
            <div className="card bg-base-200 border border-base-300">

                <div className="card-body">

                    {[...Array(5)].map((_, index) => (

                        <div
                            key={index}
                            className="
                                flex
                                items-center
                                justify-between
                                py-4
                                border-b
                                border-base-300
                            "
                        >

                            <div className="skeleton h-6 w-32"></div>

                            <div className="skeleton h-6 w-24"></div>

                            <div className="skeleton h-6 w-28"></div>

                            <div className="skeleton h-6 w-20"></div>

                            <div className="skeleton h-8 w-24"></div>

                        </div>

                    ))}

                </div>

            </div>

        </div>

    )

}