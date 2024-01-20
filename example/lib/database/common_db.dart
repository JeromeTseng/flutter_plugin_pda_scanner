// 定义一个异步函数来获取数据库路径

import 'package:sqflite/sqflite.dart';

import '../utils/db_utils.dart';

class CommonDBManager{
  static Database? _instance;

  static Future<Database> get instance async{
    _instance ??= await _createCommonDatabase();
    return Future.value(_instance);
  }

  static Future<Database> _createCommonDatabase() async {
    // 获取数据库路径
    final path = await getDatabasePath('common.db');

    // 打开数据库
    final database = openDatabase(
      path,
      version: 1,
      // 当数据库第一次被创建时，执行创建表的操作
      onCreate: (db, version) {
        db.execute(TABLE_CREATE_SQL);
      },
    );
    return database;
  }

}



const String TABLE_CREATE_SQL = """
  CREATE TABLE device_log(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT,content TEXT,type TEXT)
""";