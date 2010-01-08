require "net/http"
require "uri"

host = "localhost:3997"
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

