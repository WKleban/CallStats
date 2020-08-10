package pl.wotu.callstats;

public class SmsModel {

    private String _id;
    private String _address;
    private String _msg;
    private String _readState; //"0" for have not read sms and "1" for have read sms
    private String _time;
    private String _folderName;

    public SmsModel() {
    }

    public SmsModel(String _id, String _address, String _msg, String _readState, String _time, String _folderName) {
        this._id = _id;
        this._address = _address;
        this._msg = _msg;
        this._readState = _readState;
        this._time = _time;
        this._folderName = _folderName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_msg() {
        return _msg;
    }

    public void set_msg(String _msg) {
        this._msg = _msg;
    }

    public String get_readState() {
        return _readState;
    }

    public void set_readState(String _readState) {
        this._readState = _readState;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public String get_folderName() {
        return _folderName;
    }

    public void set_folderName(String _folderName) {
        this._folderName = _folderName;
    }
}
