#!/bin/bash

# GitHub Release 创建脚本 - v1.0.1

echo "🚀 创建 Smart Editor Switcher v1.0.1 Release..."

# 检查是否已登录 GitHub
if ! gh auth status > /dev/null 2>&1; then
    echo "❌ 请先登录 GitHub CLI:"
    echo "   gh auth login"
    exit 1
fi

# 检查插件包是否存在
if [ ! -f "build/distributions/editor-switcher-1.0.1.zip" ]; then
    echo "❌ 插件包不存在，请先构建插件:"
    echo "   ./gradlew clean buildPlugin"
    exit 1
fi

# 创建 GitHub Release
echo "📦 创建 GitHub Release..."
gh release create v1.0.1 \
    --title "Smart Editor Switcher v1.0.1 - API 兼容性修复" \
    --notes-file release_notes_v1.0.1.md \
    --latest \
    build/distributions/editor-switcher-1.0.1.zip

if [ $? -eq 0 ]; then
    echo "✅ Release 创建成功！"
    echo "🔗 查看 Release: https://github.com/yanxxcloud/smart-editor-switcher/releases/tag/v1.0.1"
else
    echo "❌ Release 创建失败"
    exit 1
fi