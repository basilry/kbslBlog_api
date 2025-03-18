# 프론트엔드에서 이미지 프록시 사용하기

## 1. 기본 사용법

백엔드에서 이미지 업로드 후 받는 URL은 이미 프록시 URL 형식으로 변환되어있습니다:
```
/proxy/image/{fileId}
```

이 URL을 그대로 `<img>` 태그에 사용할 수 있습니다.

```jsx
<img src="/proxy/image/1ABCDE12345" alt="이미지 설명" />
```

## 2. TipTap 에디터에서 사용

TipTap 에디터에서는 이미 백엔드에서 프록시 URL로 변환된 이미지를 그대로 사용할 수 있습니다.

```jsx
import { Editor, EditorContent } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Image from '@tiptap/extension-image';

const Tiptap = ({ content }) => {
  const editor = useEditor({
    extensions: [
      StarterKit,
      Image,
    ],
    content: content, // 이미 프록시 URL을 포함하는 내용
  });

  return <EditorContent editor={editor} />;
};
```

## 3. 기존 구글 드라이브 URL 변환

이미 구글 드라이브 URL을 사용하는 기존 콘텐츠가 있는 경우:

```jsx
// 구글 드라이브 URL을 프록시 URL로 변환
const convertToProxyUrl = (content) => {
  if (!content) return '';
  
  // 구글 드라이브 URL 패턴 (id= 뒤의 파일 ID 추출)
  const pattern = /https:\/\/drive\.google\.com\/uc\?id=([a-zA-Z0-9_-]+)/g;
  
  // 모든 매칭 URL을 프록시 URL로 대체
  return content.replace(pattern, (match, fileId) => {
    return `/proxy/image/${fileId}`;
  });
};

const Article = ({ htmlContent }) => {
  const convertedContent = convertToProxyUrl(htmlContent);
  
  return (
    <div 
      dangerouslySetInnerHTML={{ __html: convertedContent }} 
      className="article-content"
    />
  );
};
```

## 4. 이미지 업로드 핸들러 예제

```jsx
const uploadImage = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const response = await fetch('/file/upload', {
      method: 'POST',
      body: formData,
      headers: {
        'Authorization': `Bearer ${getToken()}` // 토큰 함수는 구현 필요
      }
    });
    
    if (!response.ok) throw new Error('이미지 업로드 실패');
    
    const data = await response.json();
    // 반환된 URL은 이미 프록시 URL 형식
    return data.url; // 예: '/proxy/image/1ABCDE12345'
  } catch (error) {
    console.error('이미지 업로드 오류:', error);
    throw error;
  }
};
```

## 5. TipTap 이미지 업로드 버튼 구현

```jsx
const ImageUploadButton = ({ editor }) => {
  const handleUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;
    
    try {
      const url = await uploadImage(file);
      
      // 에디터에 이미지 삽입
      editor.chain().focus().setImage({ src: url }).run();
    } catch (e) {
      alert('이미지 업로드 실패');
    }
  };
  
  return (
    <button 
      onClick={() => document.getElementById('image-upload').click()}
      className="image-button"
    >
      이미지 추가
      <input
        id="image-upload"
        type="file"
        accept="image/*"
        onChange={handleUpload}
        style={{ display: 'none' }}
      />
    </button>
  );
};
```

## 6. 외부 URL로 이미지 추가 (TipTap)

```jsx
const handleExternalImage = async (url) => {
  try {
    // 백엔드에 외부 URL 전송
    const response = await fetch('/file/external-urls', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({ urls: [url] })
    });
    
    if (!response.ok) throw new Error('외부 이미지 업로드 실패');
    
    const data = await response.json();
    // 반환된 URL은 이미 프록시 URL 형식
    return data.urls[0]; // 예: '/proxy/image/1ABCDE12345'
  } catch (error) {
    console.error('외부 이미지 업로드 오류:', error);
    throw error;
  }
};
```

## 7. URL 정규화 유틸리티 함수

```js
/**
 * 이미지 URL을 표준화하는 함수
 * 구글 드라이브 URL인 경우 프록시 URL로 변환
 */
export const normalizeImageUrl = (url) => {
  if (!url) return '';
  
  // 이미 프록시 URL인 경우
  if (url.startsWith('/proxy/')) {
    return url;
  }
  
  // 구글 드라이브 URL인 경우
  const googleDriveMatch = url.match(/https:\/\/drive\.google\.com\/uc\?id=([a-zA-Z0-9_-]+)/);
  if (googleDriveMatch) {
    return `/proxy/image/${googleDriveMatch[1]}`;
  }
  
  // 그 외의 경우는 원래 URL 반환
  return url;
};
``` 