#!/usr/bin/env python3
"""
Flutter APK 分析自动化脚本
用于批量处理 APK 文件，执行 Blutter 反编译和 Glasswing 分析
"""

import os
import sys
import hashlib
import shutil
import subprocess
import yaml
import zipfile
import glob
from pathlib import Path
from typing import List, Dict, Any


class FlutterAPKAnalyzer:
    def __init__(self, apk_dir: str, output_dir: str):
        """
        初始化分析器
        
        Args:
            apk_dir: APK 文件目录
            output_dir: 输出目录
        """
        self.apk_dir = Path(apk_dir).resolve()
        self.output_dir = Path(output_dir).resolve()
        self.script_dir = Path(__file__).parent.resolve()
        
        # 工具路径
        self.blutter_script = self.script_dir / "blutter-patch" / "blutter.py"
        self.glasswing_jar = self.script_dir / "Glasswing.jar"
        self.base_config_path = self.script_dir / "flowdroid-config.yaml"
        
        # 确保输出目录存在
        self.output_dir.mkdir(parents=True, exist_ok=True)
        
        print(f"APK 目录: {self.apk_dir}")
        print(f"输出目录: {self.output_dir}")
        print(f"Blutter 脚本: {self.blutter_script}")
        print(f"Glasswing JAR: {self.glasswing_jar}")
        
    def get_apk_files(self) -> List[Path]:
        """获取所有 APK 文件"""
        apk_files = []
        for pattern in ["*.apk", "*.APK"]:
            apk_files.extend(self.apk_dir.glob(pattern))
        
        print(f"找到 {len(apk_files)} 个 APK 文件")
        return sorted(apk_files)
    
    def calculate_file_hash(self, file_path: Path) -> str:
        """计算文件的 SHA256 哈希值"""
        sha256_hash = hashlib.sha256()
        with open(file_path, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                sha256_hash.update(chunk)
        return sha256_hash.hexdigest()[:16]  # 使用前16个字符作为目录名
    
    def extract_apk(self, apk_path: Path, extract_dir: Path) -> bool:
        """解压 APK 文件"""
        try:
            print(f"正在解压 APK: {apk_path.name}")
            with zipfile.ZipFile(apk_path, 'r') as zip_ref:
                zip_ref.extractall(extract_dir)
            print(f"APK 解压完成: {extract_dir}")
            return True
        except Exception as e:
            print(f"解压 APK 失败: {e}")
            return False
    
    def find_arm64_lib_dir(self, extract_dir: Path) -> Path:
        """查找 lib/arm64-v8a 目录"""
        possible_paths = [
            extract_dir / "lib" / "arm64-v8a",
            extract_dir / "assets" / "lib" / "arm64-v8a"
        ]
        
        # 搜索所有可能的 lib/arm64-v8a 目录
        for root, dirs, files in os.walk(extract_dir):
            if "arm64-v8a" in dirs:
                lib_path = Path(root) / "arm64-v8a"
                if lib_path.exists():
                    print(f"找到 arm64-v8a 目录: {lib_path}")
                    return lib_path
        
        # 如果没找到，返回第一个可能的路径（即使不存在）
        for path in possible_paths:
            if path.exists():
                print(f"找到 arm64-v8a 目录: {path}")
                return path
        
        raise FileNotFoundError(f"在 {extract_dir} 中未找到 lib/arm64-v8a 目录")
    
    def run_blutter(self, lib_dir: Path, output_dir: Path) -> bool:
        """运行 Blutter 分析"""
        try:
            print(f"正在运行 Blutter 分析...")
            print(f"输入目录: {lib_dir}")
            print(f"输出目录: {output_dir}")
            
            cmd = [
                sys.executable,
                str(self.blutter_script),
                str(lib_dir),
                str(output_dir)
            ]
            
            print(f"执行命令: {' '.join(cmd)}")
            result = subprocess.run(cmd, capture_output=True, text=True, cwd=self.script_dir)
            
            if result.returncode == 0:
                print("Blutter 分析完成")
                return True
            else:
                print(f"Blutter 分析失败:")
                print(f"stdout: {result.stdout}")
                print(f"stderr: {result.stderr}")
                return False
                
        except Exception as e:
            print(f"运行 Blutter 时出错: {e}")
            return False
    
    def load_base_config(self) -> Dict[str, Any]:
        """加载基础配置文件"""
        try:
            with open(self.base_config_path, 'r', encoding='utf-8') as f:
                config = yaml.safe_load(f)
            return config
        except Exception as e:
            print(f"加载基础配置文件失败: {e}")
            return {}
    
    def create_glasswing_config(self, apk_path: Path, work_dir: Path, 
                              preprocessor_type: str) -> Path:
        """为 Glasswing 创建配置文件"""
        config = self.load_base_config()
        
        # 更新配置
        config.update({
            'apkPath': str(apk_path),
            'outputFile': str(work_dir / "glasswing_output"),
            'coreIRDir': str(work_dir / "coreirtmpBatch" / "out"),
            'resultFile': str(work_dir / "result"),
            'preprocessorType': preprocessor_type,
            'blutterOutputDir': str(work_dir / "blutterout"),
            'dsirOutputDir': str(work_dir / "coreirtmpBatch")
        })
        
        # 保存配置文件
        config_filename = f"flowdroid-config-{preprocessor_type}.yaml"
        config_path = work_dir / config_filename
        
        with open(config_path, 'w', encoding='utf-8') as f:
            yaml.dump(config, f, default_flow_style=False, allow_unicode=True)
        
        print(f"创建配置文件: {config_path}")
        return config_path
    
    def run_glasswing(self, config_path: Path) -> bool:
        """运行 Glasswing 分析"""
        try:
            print(f"正在运行 Glasswing 分析...")
            print(f"配置文件: {config_path}")
            
            cmd = [
                "java", "-jar", str(self.glasswing_jar),
                str(config_path)
            ]
            
            print(f"执行命令: {' '.join(cmd)}")
            result = subprocess.run(cmd, capture_output=True, text=True, cwd=self.script_dir)
            
            if result.returncode == 0:
                print("Glasswing 分析完成")
                print(f"stdout: {result.stdout}")
                return True
            else:
                print(f"Glasswing 分析失败:")
                print(f"stdout: {result.stdout}")
                print(f"stderr: {result.stderr}")
                return False
                
        except Exception as e:
            print(f"运行 Glasswing 时出错: {e}")
            return False
    
    def setup_work_directory(self, work_dir: Path):
        """设置工作目录结构"""
        dirs_to_create = [
            work_dir / "blutterout",
            work_dir / "coreirtmpBatch",
            work_dir / "result"
        ]
        
        for dir_path in dirs_to_create:
            dir_path.mkdir(parents=True, exist_ok=True)
            print(f"创建目录: {dir_path}")
    
    def process_single_apk(self, apk_path: Path) -> bool:
        """处理单个 APK 文件"""
        print(f"\n{'='*60}")
        print(f"开始处理 APK: {apk_path.name}")
        print(f"{'='*60}")
        
        try:
            # 1. 创建工作目录
            apk_hash = self.calculate_file_hash(apk_path)
            work_dir = self.output_dir / apk_hash
            work_dir.mkdir(parents=True, exist_ok=True)
            
            print(f"APK 哈希: {apk_hash}")
            print(f"工作目录: {work_dir}")
            
            # 记录 APK 原始路径
            with open(work_dir / "apk_info.txt", 'w') as f:
                f.write(f"Original APK Path: {apk_path}\n")
                f.write(f"APK Hash: {apk_hash}\n")
                f.write(f"APK Size: {apk_path.stat().st_size} bytes\n")
            
            # 2. 设置工作目录结构
            self.setup_work_directory(work_dir)
            
            # 3. 解压 APK
            extract_dir = work_dir / "extracted"
            if not self.extract_apk(apk_path, extract_dir):
                return False
            
            # 4. 查找 arm64-v8a 目录
            try:
                lib_dir = self.find_arm64_lib_dir(extract_dir)
            except FileNotFoundError as e:
                print(f"跳过此 APK: {e}")
                return False
            
            # 5. 运行 Blutter 分析
            blutter_output = work_dir / "blutterout"
            if not self.run_blutter(lib_dir, blutter_output):
                print("Blutter 分析失败，跳过 Glasswing 分析")
                return False
            
            # 6. 运行 Glasswing 分析 (先 core 后 raw)
            success = True
            
            # Core 分析
            print(f"\n{'-'*40}")
            print("开始 Core 分析")
            print(f"{'-'*40}")
            
            core_config = self.create_glasswing_config(apk_path, work_dir, "core")
            if not self.run_glasswing(core_config):
                print("Core 分析失败")
                success = False
            
            # Raw 分析
            print(f"\n{'-'*40}")
            print("开始 Raw 分析")
            print(f"{'-'*40}")
            
            raw_config = self.create_glasswing_config(apk_path, work_dir, "raw")
            if not self.run_glasswing(raw_config):
                print("Raw 分析失败")
                success = False
            
            if success:
                print(f"\n✅ APK {apk_path.name} 处理完成")
            else:
                print(f"\n❌ APK {apk_path.name} 处理部分失败")
            
            return success
            
        except Exception as e:
            print(f"处理 APK {apk_path.name} 时出错: {e}")
            return False
    
    def analyze_all_apks(self):
        """分析所有 APK 文件"""
        apk_files = self.get_apk_files()
        
        if not apk_files:
            print("未找到任何 APK 文件")
            return
        
        successful = 0
        failed = 0
        
        for i, apk_path in enumerate(apk_files, 1):
            print(f"\n进度: {i}/{len(apk_files)}")
            
            if self.process_single_apk(apk_path):
                successful += 1
            else:
                failed += 1
        
        print(f"\n{'='*60}")
        print("分析完成统计:")
        print(f"总计: {len(apk_files)} 个 APK")
        print(f"成功: {successful} 个")
        print(f"失败: {failed} 个")
        print(f"{'='*60}")


def main():
    """主函数"""
    import argparse
    
    parser = argparse.ArgumentParser(description="Flutter APK 分析自动化工具")
    parser.add_argument("apk_dir", help="APK 文件目录")
    parser.add_argument("output_dir", help="输出目录")
    parser.add_argument("--verbose", "-v", action="store_true", help="详细输出")
    
    args = parser.parse_args()
    
    if not os.path.exists(args.apk_dir):
        print(f"错误: APK 目录不存在: {args.apk_dir}")
        sys.exit(1)
    
    try:
        analyzer = FlutterAPKAnalyzer(args.apk_dir, args.output_dir)
        analyzer.analyze_all_apks()
    except KeyboardInterrupt:
        print("\n用户中断分析")
        sys.exit(1)
    except Exception as e:
        print(f"分析过程中发生错误: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
