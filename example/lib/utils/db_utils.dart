import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';
import 'package:pda_scanner_example/database/common_db.dart';
import 'package:sqflite/sqflite.dart';

Future<String> getDatabasePath(String dbName) async {
  // 获取应用的文档目录
  final directory = await getApplicationDocumentsDirectory();
  // 拼接路径
  final path = join(directory.path, dbName);
  return path;
}

