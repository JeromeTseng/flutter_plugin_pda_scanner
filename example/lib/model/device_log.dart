import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:pda_scanner/pda_scanner.dart';
import 'package:pda_scanner_example/database/common_db.dart';
import 'package:shared_preferences/shared_preferences.dart';

class DeviceLog {
  static const tableName = "device_log";

  int? _id;
  DateTime? _time;
  String? _content;
  LogType? _type; // 0 是错误日志  1 是info日志

  DeviceLog(this._time, this._content,this._type);

  DeviceLog.fromSQLMap(Map<String, dynamic> data) {
    _id = data['id'];
    _time = DateTime.parse(data['time']);
    _content = data['content'];
    _type = data['type'] == LogType.info.name ? LogType.info : LogType.error;
  }

  int? get id => _id;

  String? get time => _time?.toString().split('.').first;

  String? get content => _content??"无日志内容";

  String? get type => _type?.name;

  // 将对象转换为Map
  Map<String, dynamic> _toSQLMap() {
    return {
      'time': _time?.toString(),
      'content': _content,
      'type':_type?.name
    };
  }

  DeviceLog.fromScannerLogMap(Map<String,dynamic> map){
    _time = DateTime.fromMillisecondsSinceEpoch(int.parse(map['time']));
    _content = map['content'];
    _type = map['type'] == LogType.info.name ? LogType.info : LogType.error;
  }

  Future<void> insertOne() async {
    try {
      var db = await CommonDBManager.instance;
      db.insert(tableName, _toSQLMap());
    } catch (ex) {
      if (kDebugMode) {
        log("保存错误日志时出现错误：$ex");
      }
    }
  }

  static Future<List<DeviceLog>> getAllLogs() async {
    int saveCount= 100;
    var db = await CommonDBManager.instance;
    var records = await db.query(
      tableName,
      columns: ['id', 'time', 'content','type'],
      orderBy: 'id desc',
      limit: saveCount
    );
    if(records.length == saveCount){
      // 拿到最后的一条记录ID
      int lastId = (records.last['id'] as int?) ?? 0 ;
      // 删除该类型ID小于该ID的所有记录
      await _deleteOutRange(lastId);
    }
    final localRecord = records.map((el) => DeviceLog.fromSQLMap(el));
    List<DeviceLog> pdaScannerLogs = (await PdaScanner.getPDAInitLog()).map((e) => DeviceLog.fromScannerLogMap(e))
        .toList();
    pdaScannerLogs.sort(sortByTime);
    final results = localRecord.toList();
    results.insertAll(0, pdaScannerLogs);
    return results;
  }

  // 意思就是保留同类型最近一百条数据 不然日志太多了
  static Future<int> _deleteOutRange(int id) async{
    var db = await CommonDBManager.instance;
    return await db.delete(tableName,where: "id < ?",whereArgs: [id]);
  }
}

enum LogType {
  error,
  info
}

int sortByTime(DeviceLog pre,DeviceLog now){
  if(pre.time != null && now.time != null){
    return now.time!.compareTo(pre.time!);
  }
  return 0;
}