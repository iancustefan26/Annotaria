import os
import requests

# python3 -m venv ~/venvs/yt_memes_env
# source ~/venvs/yt_memes_env/bin/activate


# Your Pexels API key here
PEXELS_API_KEY = '4ASODLqGfWD7W8loiQMobLZYSHqEBcWD2ZhQyhmnSCnpYxU9lZ7QZLXD'

os.makedirs("photos", exist_ok=True)
os.makedirs("videos", exist_ok=True)

HEADERS = {
    "Authorization": PEXELS_API_KEY
}

def download_file(url, output_path):
    try:
        response = requests.get(url, stream=True)
        response.raise_for_status()
        with open(output_path, 'wb') as f:
            for chunk in response.iter_content(1024):
                f.write(chunk)
    except Exception as e:
        print(f"Download failed: {e}")

def download_photos(count=50):
    print(f"Downloading {count} photos...")
    per_page = 80
    downloaded = 0
    page = 1

    while downloaded < count:
        resp = requests.get(f"https://api.pexels.com/v1/curated?per_page={per_page}&page={page}", headers=HEADERS)
        data = resp.json()

        for photo in data.get('photos', []):
            if downloaded >= count:
                break
            img_url = photo['src']['original']
            filename = f"photos/photo{downloaded + 1}.jpg"
            if not os.path.exists(filename):
                print(f"Downloading {filename}")
                download_file(img_url, filename)
                downloaded += 1

        page += 1
        if not data.get('photos'):
            break

def download_videos(count=50):
    print(f"Downloading {count} short videos...")
    per_page = 80
    downloaded = 0
    page = 1

    while downloaded < count:
        resp = requests.get(f"https://api.pexels.com/videos/popular?per_page={per_page}&page={page}", headers=HEADERS)
        data = resp.json()

        for video in data.get('videos', []):
            if downloaded >= count:
                break
            # Only download videos <= 15 seconds
            if video['duration'] <= 15:
                url = video['video_files'][0]['link']
                filename = f"videos/video{downloaded + 1}.mp4"
                if not os.path.exists(filename):
                    print(f"Downloading {filename}")
                    download_file(url, filename)
                    downloaded += 1

        page += 1
        if not data.get('videos'):
            break

if __name__ == "__main__":
    download_photos(50)
    download_videos(50)
