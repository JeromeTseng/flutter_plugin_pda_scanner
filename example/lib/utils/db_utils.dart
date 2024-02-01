import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

Future<String> getDatabasePath(String dbName) async {
  // 获取应用的文档目录
  final directory = await getApplicationDocumentsDirectory();
  // 拼接路径
  final path = join(directory.path, dbName);
  return path;
}

