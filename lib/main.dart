import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const BlockSitesApp());
}

class BlockSitesApp extends StatelessWidget {
  const BlockSitesApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Block Sites',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const HomeScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  static const platform = MethodChannel('com.example.accessibility/channel');
  bool isAccessibilityEnabled = false;

  @override
  void initState() {
    super.initState();
    checkAccessibilityStatus();
  }

  Future<void> checkAccessibilityStatus() async {
    try {
      final bool status = await platform.invokeMethod('isAccessibilityEnabled');
      setState(() {
        isAccessibilityEnabled = status;
      });
    } on PlatformException catch (e) {
      debugPrint("Error checking accessibility: ${e.message}");
    }
  }

  Future<void> openAccessibilitySettings() async {
    try {
      await platform.invokeMethod('openAccessibilitySettings');
    } on PlatformException catch (e) {
      debugPrint("Error opening settings: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Block Distracting Sites'),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                isAccessibilityEnabled ? Icons.check_circle : Icons.block,
                size: 72,
                color: isAccessibilityEnabled ? Colors.green : Colors.red,
              ),
              const SizedBox(height: 16),
              Text(
                isAccessibilityEnabled
                    ? 'Accessibility Service is ON'
                    : 'Please enable Accessibility to block sites.',
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 18,
                  color: isAccessibilityEnabled ? Colors.green : Colors.red,
                ),
              ),
              const SizedBox(height: 32),
              ElevatedButton.icon(
                onPressed: openAccessibilitySettings,
                icon: const Icon(Icons.settings_accessibility),
                label: const Text('Open Accessibility Settings'),
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
