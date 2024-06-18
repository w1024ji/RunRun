import android.os.Parcel
import android.os.Parcelable

data class BusParcelable(
    val arsId: String,
    val nodeId: String,
    val routeId: String,
    val xCoordinate: String,
    val yCoordinate: String,
    val routeName: String,
    val sequence: String,
    val stationName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(arsId)
        parcel.writeString(nodeId)
        parcel.writeString(routeId)
        parcel.writeString(xCoordinate)
        parcel.writeString(yCoordinate)
        parcel.writeString(routeName)
        parcel.writeString(sequence)
        parcel.writeString(stationName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BusParcelable> {
        override fun createFromParcel(parcel: Parcel): BusParcelable {
            return BusParcelable(parcel)
        }

        override fun newArray(size: Int): Array<BusParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
