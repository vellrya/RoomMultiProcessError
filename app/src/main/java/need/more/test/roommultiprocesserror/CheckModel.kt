package need.more.test.roommultiprocesserror

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checks")
data class CheckModel(val ts: Long) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}
