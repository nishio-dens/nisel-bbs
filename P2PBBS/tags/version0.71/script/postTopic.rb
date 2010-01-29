#指定したホストに向かって沢山のトピック・コメントを投稿するスクリプト
#使い方

#hosts.txt を用意
#このtxtには ホストアドレス:ポート番号が記述されている
#
#hosts.txtの例）
# 133.23.222.179:40001
# 133.23.222.179:40002
#
#その後， cat hosts.txt | ruby postTopic.rb 等と指定すればよい

require "net/http"
require "uri"

while line = gets
	#末尾の改行削除
	line.chomp!

	host = line
	uri = URI.parse("http://" + host + "/command/manage/")
	writeuri = URI.parse("http://" + host + "/command/write/")
	10.times {|i|
		category = nil
		topic = nil
	
		Net::HTTP.start(uri.host, uri.port){ |http|
			body = "category=テストカテゴリ&title=テストトピック" \
				+ i.to_s \
				+ " from " \
				+ host \
				+ "&mail=test@test.com&password=test&message=" \
				+ "これはテストトピックです．"
			#データ送信
			response = http.post(uri.path, body)
			res = response.body.split(" ")
			category = res[2]
			topic = res[4]
		}
		20.times{|j|
			Net::HTTP.start(writeuri.host, writeuri.port){ |http|
				body = "category=" + category \
					+ "&topic=" + topic \
					+ "&author=System" \
					+ "&mail=test@test.com" \
					+ "&message=これはテストメッセージ" \
					+ j.to_s \
					+ "です．"
		
				response = http.post(writeuri.path, body)
			}
		}
	}
end