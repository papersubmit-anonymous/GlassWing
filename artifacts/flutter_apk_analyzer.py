#!/usr/bin/env python3

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
        self.apk_dir = Path(apk_dir).resolve()
        self.output_dir = Path(output_dir).resolve()
        self.script_dir = Path(__file__).parent.resolve()
        
        self.blutter_script = self.script_dir / "blutter-patch" / "blutter.py"
        self.glasswing_jar = self.script_dir / "Glasswing.jar"
        self.base_config_path = self.script_dir / "flowdroid-config.yaml"
        
        self.output_dir.mkdir(parents=True, exist_ok=True)
        
        print(f"APK directory: {self.apk_dir}")
        print(f"Output directory: {self.output_dir}")
        print(f"Blutter script: {self.blutter_script}")
        print(f"Glasswing JAR: {self.glasswing_jar}")
        
    def get_apk_files(self) -> List[Path]:
        apk_files = []
        for pattern in ["*.apk", "*.APK"]:
            apk_files.extend(self.apk_dir.glob(pattern))
        
        print(f"Found {len(apk_files)} APK files")
        return sorted(apk_files)
    
    def calculate_file_hash(self, file_path: Path) -> str:
        sha256_hash = hashlib.sha256()
        with open(file_path, "rb") as f:
            for chunk in iter(lambda: f.read(4096), b""):
                sha256_hash.update(chunk)
        return sha256_hash.hexdigest()[:16] 
    
    def extract_apk(self, apk_path: Path, extract_dir: Path) -> bool:
        try:
            print(f"Extracting APK: {apk_path.name}")
            with zipfile.ZipFile(apk_path, 'r') as zip_ref:
                zip_ref.extractall(extract_dir)
            print(f"APK extraction complete: {extract_dir}")
            return True
        except Exception as e:
            print(f"Failed to extract APK: {e}")
            return False
    
    def find_arm64_lib_dir(self, extract_dir: Path) -> Path:
        """Find lib/arm64-v8a directory"""
        possible_paths = [
            extract_dir / "lib" / "arm64-v8a",
            extract_dir / "assets" / "lib" / "arm64-v8a"
        ]
        
        for root, dirs, files in os.walk(extract_dir):
            if "arm64-v8a" in dirs:
                lib_path = Path(root) / "arm64-v8a"
                if lib_path.exists():
                    print(f"Found arm64-v8a directory: {lib_path}")
                    return lib_path
        
        for path in possible_paths:
            if path.exists():
                print(f"Found arm64-v8a directory: {path}")
                return path
        
        raise FileNotFoundError(f"lib/arm64-v8a directory not found in {extract_dir}")
    
    def run_blutter(self, lib_dir: Path, output_dir: Path) -> bool:
        try:
            print(f"Running Blutter analysis...")
            print(f"Input directory: {lib_dir}")
            print(f"Output directory: {output_dir}")
            
            cmd = [
                sys.executable,
                str(self.blutter_script),
                str(lib_dir),
                str(output_dir)
            ]
            
            print(f"Executing command: {' '.join(cmd)}")
            result = subprocess.run(cmd, capture_output=True, text=True, cwd=self.script_dir)
            
            if result.returncode == 0:
                print("Blutter analysis complete")
                return True
            else:
                print(f"Blutter analysis failed:")
                print(f"stdout: {result.stdout}")
                print(f"stderr: {result.stderr}")
                return False
                
        except Exception as e:
            print(f"Error running Blutter: {e}")
            return False
    
    def load_base_config(self) -> Dict[str, Any]:
        try:
            with open(self.base_config_path, 'r', encoding='utf-8') as f:
                config = yaml.safe_load(f)
            return config
        except Exception as e:
            print(f"Failed to load base config file: {e}")
            return {}
    
    def create_glasswing_config(self, apk_path: Path, work_dir: Path, 
                              preprocessor_type: str) -> Path:
        config = self.load_base_config()
        
        config.update({
            'apkPath': str(apk_path),
            'outputFile': str(work_dir / "glasswing_output"),
            'coreIRDir': str(work_dir / "coreirtmpBatch" / "out"),
            'resultFile': str(work_dir / "result"),
            'preprocessorType': preprocessor_type,
            'blutterOutputDir': str(work_dir / "blutterout"),
            'dsirOutputDir': str(work_dir / "coreirtmpBatch")
        })
        
        config_filename = f"flowdroid-config-{preprocessor_type}.yaml"
        config_path = work_dir / config_filename
        
        with open(config_path, 'w', encoding='utf-8') as f:
            yaml.dump(config, f, default_flow_style=False, allow_unicode=True)
        
        print(f"Created config file: {config_path}")
        return config_path
    
    def run_glasswing(self, config_path: Path) -> bool:
        try:
            print(f"Running Glasswing analysis...")
            print(f"Config file: {config_path}")
            
            cmd = [
                "java", "-jar", str(self.glasswing_jar),
                str(config_path)
            ]
            
            print(f"Executing command: {' '.join(cmd)}")
            result = subprocess.run(cmd, capture_output=True, text=True, cwd=self.script_dir)
            
            if result.returncode == 0:
                print("Glasswing analysis complete")
                print(f"stdout: {result.stdout}")
                return True
            else:
                print(f"Glasswing analysis failed:")
                print(f"stdout: {result.stdout}")
                print(f"stderr: {result.stderr}")
                return False
                
        except Exception as e:
            print(f"Error running Glasswing: {e}")
            return False
    
    def setup_work_directory(self, work_dir: Path):
        dirs_to_create = [
            work_dir / "blutterout",
            work_dir / "coreirtmpBatch",
            work_dir / "result"
        ]
        
        for dir_path in dirs_to_create:
            dir_path.mkdir(parents=True, exist_ok=True)
            print(f"Created directory: {dir_path}")
    
    def process_single_apk(self, apk_path: Path) -> bool:
        print(f"\n{'='*60}")
        print(f"Start processing APK: {apk_path.name}")
        print(f"{'='*60}")
        
        try:
            apk_hash = self.calculate_file_hash(apk_path)
            work_dir = self.output_dir / apk_hash
            work_dir.mkdir(parents=True, exist_ok=True)
            
            print(f"APK hash: {apk_hash}")
            print(f"Working directory: {work_dir}")
            
            with open(work_dir / "apk_info.txt", 'w') as f:
                f.write(f"Original APK Path: {apk_path}\n")
                f.write(f"APK Hash: {apk_hash}\n")
                f.write(f"APK Size: {apk_path.stat().st_size} bytes\n")
            
            self.setup_work_directory(work_dir)
            
            extract_dir = work_dir / "extracted"
            if not self.extract_apk(apk_path, extract_dir):
                return False
            
            try:
                lib_dir = self.find_arm64_lib_dir(extract_dir)
            except FileNotFoundError as e:
                print(f"Skipping this APK: {e}")
                return False
            
            blutter_output = work_dir / "blutterout"
            if not self.run_blutter(lib_dir, blutter_output):
                print("Blutter analysis failed, skipping Glasswing analysis")
                return False
            
            success = True
            
            print(f"\n{'-'*40}")
            print("Starting Core analysis")
            print(f"{'-'*40}")
            
            core_config = self.create_glasswing_config(apk_path, work_dir, "core")
            if not self.run_glasswing(core_config):
                print("Core analysis failed")
                success = False
            
            print(f"\n{'-'*40}")
            print("Starting Raw analysis")
            print(f"{'-'*40}")
            
            raw_config = self.create_glasswing_config(apk_path, work_dir, "raw")
            if not self.run_glasswing(raw_config):
                print("Raw analysis failed")
                success = False
            
            if success:
                print(f"\n✅ APK {apk_path.name} processed successfully")
            else:
                print(f"\n❌ APK {apk_path.name} processing partially failed")
            
            return success
            
        except Exception as e:
            print(f"Error processing APK {apk_path.name}: {e}")
            return False
    
    def analyze_all_apks(self):
        apk_files = self.get_apk_files()
        
        if not apk_files:
            print("No APK files found")
            return
        
        successful = 0
        failed = 0
        
        for i, apk_path in enumerate(apk_files, 1):
            print(f"\nProgress: {i}/{len(apk_files)}")
            
            if self.process_single_apk(apk_path):
                successful += 1
            else:
                failed += 1
        
        print(f"\n{'='*60}")
        print("Analysis summary:")
        print(f"Total: {len(apk_files)} APKs")
        print(f"Successful: {successful}")
        print(f"Failed: {failed}")
        print(f"{'='*60}")


def main():
    import argparse
    
    parser = argparse.ArgumentParser(description="Flutter APK Analysis Automation Tool")
    parser.add_argument("apk_dir", help="Directory containing APK files")
    parser.add_argument("output_dir", help="Output directory")
    parser.add_argument("--verbose", "-v", action="store_true", help="Verbose output")
    
    args = parser.parse_args()
    
    if not os.path.exists(args.apk_dir):
        print(f"Error: APK directory not found: {args.apk_dir}")
        sys.exit(1)
    
    try:
        analyzer = FlutterAPKAnalyzer(args.apk_dir, args.output_dir)
        analyzer.analyze_all_apks()
    except KeyboardInterrupt:
        print("\nAnalysis interrupted by user")
        sys.exit(1)
    except Exception as e:
        print(f"An error occurred during analysis: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
