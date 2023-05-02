package need.more.test.roommultiprocesserror

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CheckDao {

    @Insert
    suspend fun insert(model: CheckModel): Long

    @Query("Select * from checks order by id desc limit 1")
    fun getLatestLiveData(): LiveData<CheckModel?>

    @Query("Select * from checks order by id desc limit 1")
    suspend fun getLatestManual(): CheckModel?

}