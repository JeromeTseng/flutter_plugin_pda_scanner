import 'package:bruno/bruno.dart';
import 'package:flutter/material.dart';
import 'package:pda_scanner_example/model/device_log.dart';

class DeviceLogPage extends StatefulWidget {
  static const routeName = "/DeviceLog";

  const DeviceLogPage({super.key});

  @override
  State<DeviceLogPage> createState() => _DeviceLogPageState();
}

class _DeviceLogPageState extends State<DeviceLogPage> {
  final deviceLogs = [];

  @override
  void initState() {
    super.initState();
    init();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: buildErrorLogAppBar(),
      body: SizedBox(
          child: deviceLogs.isEmpty
              ? BrnAbnormalStateWidget(
                  content: '目前没有系统日志喔',
                )
              : ListView.builder(
                  itemCount: deviceLogs.length,
                  itemBuilder: (context, index) {
                    return Container(
                      decoration: const BoxDecoration(
                        border: Border(
                          bottom: BorderSide(width: .8, color: Colors.blueGrey),
                        ),
                      ),
                      child: buildListTile(deviceLogs[index]),
                    );
                  })),
    );
  }

  PreferredSize buildErrorLogAppBar() {
    return BrnAppBar(
      themeData: BrnAppBarConfig.dark(),
      //文本title
      title: '系统日志',
    );
  }

  Widget buildTagIcon(String? type) {
    if (LogType.info.name == type) {
      return const Icon(
        Icons.check_circle,
        color: Colors.green,
      );
    }
    return const Icon(
      Icons.error,
      color: Colors.red,
    );
  }

  Widget buildListTile(DeviceLog logRec) {
    return ListTile(
      leading: buildTagIcon(logRec.type),
      title: Text(logRec.time!),
      subtitle: Text(
        logRec.content!,
        maxLines: 10,
        overflow: TextOverflow.ellipsis,
      ),
    );
  }

  // 获取所有日志数据
  void init() async {
    var results = await DeviceLog.getAllLogs();
    deviceLogs.addAll(results);
    setState(() {});
  }
}
